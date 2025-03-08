import { React, useState, useEffect, useRef } from "react";
import { StompSessionProvider, useStompClient, useSubscription } from "react-stomp-hooks";
import { useNavigate } from "react-router-dom";
import './chatroom.css';
import online from '../assets/graphics/online.png';
import addImg from '../assets/graphics/addImage.png';
import trashIcon from '../assets/graphics/trashIcon.png';
import reportIcon from '../assets/graphics/flag.png.png';


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
            onConnect={() => console.log("connected to websocket") }
            onDisconnect={() => console.log("disconnected from websocket")}
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
    const [anyOnline, setAnyOnline] = useState(false); //is anyone online?
    const [message, setMessage] = useState("");
    const [lastMessageId, setLastMessageId] = useState(null);
    const [gotAllMessages, setGotAllMessages] = useState(false);
    const [loading, setLoading] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    const messagesEndRef = useRef(null);
    const stompClient = useStompClient();

    useSubscription("/topic/chat", (message) => {
        const payloadData = JSON.parse(message.body);
        console.log("received message:", payloadData);
        if (payloadData.success) { //! ONLY FOR SENDS RIGHT NOW. CHECK ACTION LATER
            switch (payloadData.action) {
                case "send":
                    setMessageList((prevMessages) => [...prevMessages, payloadData.message]);
                    setHasMessages(true);
                    scrollToBottom();
                    break;
                case "delete":
                    console.log(messageList);
                    setMessageList((prevMessages) => prevMessages.filter(message => message.id !== payloadData.message.id));
                    if (messageList.length > 0) { // check if we should say that it has set messages
                        setHasMessages(true);
                    }
                    scrollToBottom();
                    break;
            }
        }
    });

    // message logic
    useEffect(() => {
        getMessageHistory(); // uncomment when we get it working
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
        setLoadingMessages(true);

        try {
            const last_message = lastMessageId || "";

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

            const data = await response.json();

            if (!response.ok) {
                throw new Error("failed to fetch messages");
            } else {
                console.log(data);
                setLastMessageId(data);
                if (data.length < 100) {
                    setGotAllMessages(true); // todo: use this flag to hide button and let user know all messages have been retrieved
                    console.log("got all messages");
                }

                if (data.length > 0) { // update last message retrieved
                    setLastMessageId(data[0].id);
                }

                setMessageList((prevMessages) => [...data, ...prevMessages]); // update message list

                if (messageList.length > 0) { // check if we should say that it has set messages
                    setHasMessages(true);
                }
            }
        } catch (error) {
            console.log(error.message); // todo: turn into a status message
        }

        setLoadingMessages(false);
        setLoading(false);
    }
    const sendMessage = (e) => {
        e.preventDefault();
        if (!stompClient) {
            console.error("STOMP client not connected");
            return;
        }

        stompClient.publish({
            destination: "/chat/message",
            body: JSON.stringify({ content: message, action: "send", token: sessionStorage.getItem("token") }),
        });

        setMessage(""); // clear after sending
    };

    const deleteMessage = (e) => {
        e.preventDefault();
        const id = e.target.id;
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
                // todo: turn into status message
            }
        } catch (error) {
            console.log(error.message);
            // todo: turn into status message
        }
    }

    if (loading) return <div className="flex justify-center items-center h-full">Loading...</div>;

    return (
        <div className="chatroomContainer">
            <div className="messagePanel">
                <div className="messageArea">
                    {hasMessages && messageList.map((message,index) => (
                        <div className="messageContainer" key={index}>
                            <img src={online} alt="Online status"/>
                            <div className="message">
                                <div className="messageHeader">
                                    <span className="messageName">{message.name}</span>
                                    <div className="timeDeleteFlag">
                                        <span className="messageTime">{message.time}</span>
                                        <img src={trashIcon} alt="Delete" id={message.id.toString()} onClick={deleteMessage} />
                                        <img src={reportIcon} alt="Report" id={message.id.toString()} onClick={reportMessage} />
                                    </div>
                                </div>
                                {/* decode HTML safely? */}
                                <p>{decodeHTMLEntities(message.text)}</p>
                            </div>
                        </div>
                    ))}
                    <div ref={messagesEndRef}/>
                </div>
                <form className="sendArea" onSubmit={sendMessage}>
                    <input type="text" placeholder="Type message here..." value={message} onChange={(e) => setMessage(e.target.value)} maxLength="2000" />
                    <button type="submit"><img src={addImg} alt="Add"/></button>
                </form>
            </div>
            <div className="memberPanel">
                <input type="text" placeholder="Search for a member's messages..."></input>
                <h1>Members Online:</h1>
                <div className="onlineMembers">
                    {anyOnline ? (
                        onlineList.map((name,index) => (
                            <div key={index} className="memberCell">
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