import { React, useState, useEffect, useRef} from "react";
import './chatroom.css';
import online from '../assets/graphics/online.png';
import addImg from '../assets/graphics/addImage.png';

const Chatroom = () => {

    const [messageList,setMessageList] = useState([]); //list to hold all messages
    const [onlineList,setOnlineList] = useState([]); //list of all members who are online
    const [hasMessages, setHasMessages] = useState(false); //have any messages been sent ever?
    const [anyOnline,setAnyOnline] = useState(false); //is anyone online?

    const messagesEndRef = useRef(null);

    useEffect(() => {
        scrollToBottom();
    },[messageList])

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    // useEffect(() => {
    //     //getMessages(); //gets messages for current user
    //     // const sampleData = ["cpapa","braindoko","nimi nightmare"]
    //     // setOnlineList(sampleData);
    //     // setAnyOnline(true);
    //     const sampleData = [
    //         {"name":"braindoko", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:47PM", "text":"LALAALALALALALALAALALALALALLALALALALALALALALALALALALALAALLAALLALA :FaunaFlushedDoodle:"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:47PM", "text":"LALALALALALALALALALAALLAALLALALALALALALALALALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALAALALALAALLAALLALALALALALALALALALALALAALLAALLALA"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:47PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:48PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:49PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:50PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:51PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:52PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:53PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:54PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:55PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:56PM", "text":"scroll"},
    //         {"name":"cpapa", "pfp":"../assets/graphics/online.png","time":"02/18/25 6:57PM", "text":"scroll"},
    //     ]
    //     setMessageList(sampleData);
    //     setHasMessages(true);
    // }, [])
    
    const getMessages = async() => {
        //async function that will get messages from the database
    }

    const sendMessage = async() => {
        //async function to send message from user
    }

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
                                    <span className="messageTime">{message.time}</span>
                                </div>
                                <p>{message.text}</p>
                            </div>
                        </div>
                    ))}
                    <div ref={messagesEndRef}/>
                </div>
                <div className="sendArea">
                    <input type="text" placeholder="Type message here..."></input>
                    <button><img src={addImg}/></button>
                </div>
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
    )
}

export default Chatroom;