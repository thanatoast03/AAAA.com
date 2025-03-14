import React from "react";
import "../modals/deleteAccountModal.css";
import { useNavigate } from "react-router-dom";

const DeleteAccountModal = ({ closeDeleteModal }) => {
    const navigate = useNavigate();
    
    const deleteAccount = async() => {
        try{
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH+"/deleteAccount", {
                method: "POST",
                headers: {
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                    "Content-Type" : "application/json",
                },
                body: JSON.stringify({
                    "accountToDelete":""
                }),
            });
            const data = await response.json();
            if(response.ok){
                sessionStorage.removeItem("token");
                navigate("/login");
            } else {
                console.log("Failed to delete account");
                console.log(data.message);
            }
        } catch (error) {
            console.log("Error deleting account " + error);
        }
    }

    return(
        <div className="deleteModalOverlay" onClick={closeDeleteModal}>
            <div className="deleteModalContent" onClick={(e) => e.stopPropagation()}>
                <div className="deleteModalMessage">
                    <h1>WARNING:</h1>
                    <p>Deleting your account will PERMANENTLY delete your messages and account. Are you sure you want to delete your account?</p>
                </div>
                <div className="deleteModalButtons">
                    <button className="deleteModalCancel" onClick={closeDeleteModal}>Cancel</button>
                    <button className="deleteModalDelete" onClick={() => deleteAccount()}>Delete my account</button>
                </div>
            </div>
        </div>
    );
}
export default DeleteAccountModal