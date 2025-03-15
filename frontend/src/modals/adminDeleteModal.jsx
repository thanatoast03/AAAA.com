import React from "react";
import "../modals/deleteAccountModal.css";

const AdminDeleteModal = ({ closeDeleteModal, name2, setUserDeleted, setUserNotDeleted }) => {

    const adminDeleteAccount = async() => {
        try {
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH + "/deleteAccount",{
                method: "POST",
                headers: {
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                    "Content-Type" : "application/json",
                },
                body: JSON.stringify({
                    "accountToDelete" : name2
                }),
            });
            const data = await response.json()
            if(response.ok){
                closeDeleteModal();
                setUserDeleted(data.message);
            } else {
                closeDeleteModal();
                setUserNotDeleted(data.message);
            }
        } catch(e) {
            closeDeleteModal();
            setUserNotDeleted("Error deleting user: " + e);
        }
    }

    return(
        <div className="deleteModalOverlay" onClick={closeDeleteModal}>
            <div className="deleteModalContent" onClick={(e) => e.stopPropagation()}>
                <div className="deleteModalMessage">
                    <h1>WARNING:</h1>
                    <p>You will PERMANENTLY delete {name2}'s account. Are you sure this is the correct account to delete?</p>
                </div>
                <div className="deleteModalButtons">
                    <button className="deleteModalCancel" onClick={closeDeleteModal}>Cancel</button>
                    <button className="deleteModalDelete" onClick={() => adminDeleteAccount()}>Yes, delete the account</button>
                </div>
            </div>
        </div>
    );
}
export default AdminDeleteModal