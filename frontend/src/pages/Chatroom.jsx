import {React, useEffect, useRef, useState} from "react";
import {StompSessionProvider, useStompClient, useSubscription} from "react-stomp-hooks";
import {useNavigate} from "react-router-dom";
import './chatroom.css';
import online from '../assets/graphics/online.png';
import addImg from '../assets/graphics/addImage.png';
import trashIcon from '../assets/graphics/trashIcon.png';
import reportIcon from '../assets/graphics/flag.png.png';
import profile from "../assets/graphics/profile-icon.png";


// safe HTML entity decoder
const decodeHTMLEntities = (text) => {
    const textArea = document.createElement('textarea');
    textArea.innerHTML = text;
    return textArea.value;
};
const Chatroom = () => {
    const navigate = useNavigate();

    const token = sessionStorage.getItem("token");
    if (!token) {
        console.error("invalid auth token");
        navigate("/login");
    }

    return (
        <StompSessionProvider
            url={process.env.REACT_APP_WS_ENDPOINT}
            connectHeaders={{'Authorization': 'Bearer ' + token }}
            heartbeatIncoming={4000}
            heartbeatOutgoing={4000}
            onConnect={() => {
                console.log("connected to websocket");
            }}
            onDisconnect={() => {
                console.log("disconnected from websocket");
            }}
            onStompError={(frame) => console.error("error:", frame)}
        >
            <ChatComponent />
        </StompSessionProvider>
    )
}

const ChatComponent = () => {
    const [messageList, setMessageList] = useState([]); //list to hold all messages
    const [onlineList, setOnlineList] = useState([]); //list of all members who are online
    const [hasMessages, setHasMessages] = useState(false); //have any messages been sent ever?
    const [message, setMessage] = useState("");
    const [lastMessageId, setLastMessageId] = useState(null);
    const [gotAllMessages, setGotAllMessages] = useState(false);
    const [loading, setLoading] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    const messagesEndRef = useRef(null);
    const stompClient = useStompClient();

    const broadcastStatus = (action) => {
        if (!stompClient) {
            console.error("STOMP client not connected");
            return;
        }

        stompClient.publish({
            destination: "/chat/online",
            body: JSON.stringify({ action: action, token: sessionStorage.getItem("token") }),
        });
    }
    useSubscription("/topic/chat", (message) => {
        const payloadData = JSON.parse(message.body);
        console.log("received message:", payloadData);
        if (payloadData.success) {
            switch (payloadData.action) {
                case "send":
                    setMessageList((prevMessages) => [...prevMessages, payloadData.message]);
                    setHasMessages(true);
                    scrollToBottom();
                    break;
                case "delete":
                    const deleteId = Number(payloadData.message.id);
                    const updatedList = messageList.filter(msg => Number(msg.id) !== deleteId);

                    if (updatedList.length === 0) {
                        setHasMessages(false);
                    }

                    setMessageList(updatedList);
                    scrollToBottom();
                    break;
            }
        }
    });

    useSubscription("/topic/online", (message) => {
        const payloadData = JSON.parse(message.body);
        switch (payloadData.action) {
            case "join": // if someone joins, add them to online member list and let them know you exist
                setOnlineList(currentList => {
                    const uniqueSet = new Set([...currentList, payloadData.username]);
                    return Array.from(uniqueSet);
                });
                broadcastStatus("exists");
                break;
            case "exists":
                console.log("received user already connected: " + payloadData.username);
                setOnlineList(currentList => {
                    const uniqueSet = new Set([...currentList, payloadData.username]);
                    if (uniqueSet.size === currentList.length) { // if no changes, no need for rerender
                        return currentList;
                    }
                    return Array.from(uniqueSet);
                });
                break;
            case "leave": // remove person from the online list if they leave
                const username = payloadData.username;
                const updatedList = onlineList.filter(person => person !== username);
                setOnlineList(updatedList);
                break;
        }
    })

    // join logic; only runs when stompClient first initialized
    useEffect(() => {
        if (stompClient){
            broadcastStatus("join");

            return () => {
                broadcastStatus("leave");
            }
        }
    }, [stompClient]);

    // message logic
    useEffect(() => {
        const initialLoad = async () => {
            await getMessageHistory();
        };

        initialLoad();
    }, []);

    useEffect(() => {
        scrollToBottom();
    },[messageList])

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const getMessageHistory = async () => {
        // http request
        // function that will get initial message history
        if (loadingMessages) {
            console.log("already loading messages, request ignored");
            return;
        }

        setLoadingMessages(true);

        try {
            const last_message = lastMessageId || null;

            const response = await fetch(process.env.REACT_APP_FETCH_PATH + "/messages/history", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + sessionStorage.getItem("token")
                },
                body: JSON.stringify({
                    "message_id": last_message
                }),
            });

            if (!response.ok) {
                throw new Error("failed to fetch messages");
            }

            const data = await response.json();

            if (data.length < 100) {
                setGotAllMessages(true);
            }

            if (data.length > 0) {
                // store last id
                setLastMessageId(data[0].id);
                setHasMessages(true);
            }

            // append and remove duplicates
            setMessageList((prevMessages) => {
                const existingIds = new Set(prevMessages.map(msg => msg.id));
                const uniqueNewMessages = data.filter(msg => !existingIds.has(msg.id));

                return [...uniqueNewMessages, ...prevMessages];
            });
        } catch (error) {
            console.error("error fetching messages:", error.message); // todo: turn into an actual error message
        } finally {
            setLoadingMessages(false);
            setLoading(false);
        }
    }
    const sendMessage = (e) => {
        e.preventDefault();
        if (!stompClient) {
            console.error("STOMP client not connected");
            return;
        }

        if (message.length === 0) return; // dont allow empty messages

        stompClient.publish({
            destination: "/chat/message",
            body: JSON.stringify({ content: message, action: "send", token: sessionStorage.getItem("token") }),
        });

        setMessage(""); // clear after sending
    };

    const deleteMessage = (e) => {
        e.preventDefault();
        const id = e.target.getAttribute('data-message-id');

        if (!stompClient) {
            console.log("STOMP client not connected");
            return;
        }

        stompClient.publish({
            destination: "/chat/message",
            body: JSON.stringify({ content: id, action: "delete", token: sessionStorage.getItem("token") }),
        });
    }

    const reportMessage = async (e) => {
        e.preventDefault();

        try {
            const id = e.target.id;
            const response = await fetch(process.env.REACT_APP_FETCH_PATH + "/messages/report", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + sessionStorage.getItem("token")
                },
                body: JSON.stringify({
                    "messageId": id
                }),
            });

            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.message); // message is json field for reason of failure
            } else {
                console.log("successfully reported message");
                alert("Message reported successfully");
            }
        } catch (error) {
            console.log(error.message);
            // todo: turn into status message
            //a
        }
    }

    if (loading) return <div className="flex justify-center items-center h-full text-white">Loading...</div>;

    return (
        <div className="chatroomContainer text-white">
            <div className="messagePanel">
                <div className="messageArea">
                    <div className="w-[90%]">
                        {gotAllMessages ?
                            <p className="flex ml-[80px] w-[90%] justify-center items-center justify-self-center mt-5">No more messages.</p>
                        :
                            <button
                                className="ml-[80px] flex w-[90%] rounded p-5 items-center justify-center bg-[#1F1F1F] mt-5 justify-self-center font-casual"
                                onClick={getMessageHistory}
                                disabled={loadingMessages}
                            >
                                Get More Messages
                            </button>
                        }

                        <hr className="my-5 ml-[80px]"/>
                    </div>

                    {hasMessages && messageList.map((message) => (
                        <div className="messageContainer" key={message.id}>
                            <img src={profile} alt="profile picture" className="max-w-[40px] max-h-[40px] mx-3"/>
                            <div className="message">
                                <div className="messageHeader">
                                    <span className="messageName ml-2">{message.name}:</span>
                                    <div className="timeDeleteFlag">
                                        <span className="messageTime">{message.time}</span>
                                        { sessionStorage.getItem("username") === message.name || sessionStorage.getItem("role") === "admin" ?
                                            <img src={trashIcon} alt="Delete" data-message-id={message.id} onClick={deleteMessage} />
                                            :
                                            <div />
                                        }
                                        { sessionStorage.getItem("username") !== message.name && sessionStorage.getItem("role") !== "admin" ?
                                            <img src={reportIcon} alt="Report" id={message.id} onClick={reportMessage} />
                                            :
                                            <div />
                                        }
                                    </div>
                                </div>
                                {/* decode HTML safely? */}
                                <p className="ml-2 font-casual text-sm pt-1 pb-2">{decodeHTMLEntities(message.text)}</p>
                            </div>
                        </div>
                    ))}
                    <div ref={messagesEndRef}/>
                </div>
                <form className="sendArea font-casual" onSubmit={sendMessage}>
                    <input type="text" placeholder="Type message here..." value={message} onChange={(e) => setMessage(e.target.value)} maxLength="2000" />
                    <button type="submit"><img src={addImg} alt="Add"/></button>
                </form>
            </div>
            <div className="memberPanel">
                <input type="text" placeholder="Search for a member's messages..."></input>
                <h1>Members Online:</h1>
                <div className="onlineMembers">
                    { onlineList.length !== 0 ? (
                        onlineList.map((name,index) => (
                            <div key={name} className="memberCell">
                                <p>{name}</p>
                                <img src={online} alt="Online"></img>
                            </div>
                        ))
                    ) : (
                        <p>No members are online</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Chatroom;