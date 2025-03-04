import React from "react";
import "../modals/deleteAccountModal.css";

const AdminDeleteModal = ({ closeDeleteModal, name2 }) => {

    return(
        <div className="deleteModalOverlay" onClick={closeDeleteModal}>
            <div className="deleteModalContent" onClick={(e) => e.stopPropagation()}>
                <div className="deleteModalMessage">
                    <h1>WARNING:</h1>
                    <p>You will PERMANENTLY delete {name2}'s account. Are you sure this is the correct account to delete?</p>
                </div>
                <div className="deleteModalButtons">
                    <button className="deleteModalCancel" onClick={closeDeleteModal}>Cancel</button>
                    <button className="deleteModalDelete">Yes, delete the account</button>
                </div>
            </div>
        </div>
    );
}
export default AdminDeleteModal