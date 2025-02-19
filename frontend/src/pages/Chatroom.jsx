import { React, useState, useEffect} from "react";
import './chatroom.css';
import online from '../assets/graphics/online.png';
import addImg from '../assets/graphics/addImage.png';

const Chatroom = () => {

    const [messageList,setMessageList] = useState([]); //list to hold all messages
    const [onlineList,setOnlineList] = useState([]); //list of all members who are online
    const [hasMessages, setHasMessages] = useState(false); //have any messages been sent ever?
    const [anyOnline,setAnyOnline] = useState(false); //is anyone online?

    useEffect(() => {
        //getMessages(); //gets messages for current user
        // const sampleData = ["cpapa","braindoko","nimi nightmare"]
        // setOnlineList(sampleData);
        // setAnyOnline(true);
        const sampleData = [{"name":"braindoko", "time":"02/18/25 6:47PM", "text":"LALAALALALALALALAALALALALALLALALALALALALALALALALALALALAALLAALLALA :FaunaFlushedDoodle:"}]
        setMessageList(sampleData);
        setHasMessages(true);
    }, [])
    
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
                        <div key={index} className="message">
                            <div className="messageHeader">
                                <span className="messageName">{message.name}</span>
                                <span className="messageTime">{message.time}</span>
                            </div>
                            <div className="messageText">
                                {message.text}
                            </div>
                        </div>
                    ))}
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