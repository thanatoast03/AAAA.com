import { React, useState, useEffect, useRef } from "react";
import { StompSessionProvider, useStompClient, useSubscription } from "react-stomp-hooks";
import { useNavigate } from "react-router-dom";
import './chatroom.css';
import online from '../assets/graphics/online.png';
import addImg from '../assets/graphics/addImage.png';
import trashIcon from '../assets/graphics/trashIcon.png';

const Chatroom = () => {
    const navigate = useNavigate();

    const token = sessionStorage.getItem("token");
    if (!token) {
        console.error("invalid auth token");
        navigate("/login");
    }
    //hello
    return (
        <StompSessionProvider
            url={process.env.REACT_APP_WS_ENDPOINT}
            connectHeaders={{'Authorization': 'Bearer ' + token }}
            heartbeatIncoming={4000}
            heartbeatOutgoing={4000}
            onConnect={() => console.log("connected to websocket")}
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
    const [connection, setConnection] = useState(null);
    const [loading, setLoading] = useState(true);
    const messagesEndRef = useRef(null);
    const stompClient = useStompClient();

    useSubscription("/topic/chat", (message) => {
        // todo: handle message logic here; handle deletes and sends
        const payloadData = JSON.parse(message.body);
        console.log("received message:", payloadData);
        setMessageList((prevMessages) => [...prevMessages, payloadData]);
        setHasMessages(true);
        scrollToBottom();
    });

    // message logic

    useEffect(() => {
        scrollToBottom();
    },[messageList])

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const getMessageHistory = (message) => {
        // http request
        // function that will get initial message history
    }

    const sendMessage = (e) => {
        e.preventDefault();
        if (!stompClient) {
            console.error("STOMP client not connected");
            return;
        }

        stompClient.publish({
            destination: "/chat/message",
            body: JSON.stringify({ content: message, action: "send" }),
        });

        setMessage(""); // clear after sending
    };

    if (loading) <div className="flex justify-center items-center h-full">Loading...</div>

    return (
        <div className="chatroomContainer">
            <div className="messagePanel">
                <div className="messageArea">
                    {hasMessages && messageList.map((message,index) => (
                        <div className="messageContainer">
                            <img src={online}/>
                            <div key={index} className="message">
                                <div className="messageHeader">
                                    <span className="messageName">{message.name}</span>
                                    <div className="timeDeleteFlag">
                                        <span className="messageTime">{message.time}</span>
                                        <img src={trashIcon}/>
                                    </div>
                                </div>
                                <p>{message.text}</p>
                            </div>
                        </div>
                    ))}
                    <div ref={messagesEndRef}/>
                </div>
                <form className="sendArea" onSubmit={sendMessage}>
                    <input type="text" placeholder="Type message here..." value={message} onChange={(e) => setMessage(e.target.value)} maxLength="2000" />
                    <button><img src={addImg}/></button>
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
                                <img src={online}></img>
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