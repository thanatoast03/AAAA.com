import React from "react";
import "../modals/deleteAccountModal.css";

const DeleteAccountModal = ({ closeDeleteModal }) => {

    return(
        <div className="deleteModalOverlay" onClick={closeDeleteModal}>
            <div className="deleteModalContent" onClick={(e) => e.stopPropagation()}>
                <div className="deleteModalMessage">
                    <h1>WARNING:</h1>
                    <p>Deleting your account will PERMANENTLY delete your messages and account. Are you sure you want to delete your account?</p>
                </div>
                <div className="deleteModalButtons">
                    <button className="deleteModalCancel" onClick={closeDeleteModal}>Cancel</button>
                    <button className="deleteModalDelete">Delete my account</button>
                </div>
            </div>
        </div>
    );
}
export default DeleteAccountModal