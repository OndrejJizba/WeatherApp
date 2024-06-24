import React, { useState } from "react";
import Modal from "react-modal";
import axios from "axios";
import "./ModalStyles.css";

const RegisterModal = ({ isOpen, onRequestClose }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post("/registration", { username, password });
      if (response.data.error) {
        setError(response.data.error);
      } else {
        setShowSuccessModal(true);
        handleRegisterSuccess();
      }
    } catch (err) {
      if (err.response && err.response.data) {
        setError(err.response.data.error || err.response.data.username || err.response.data.password || "An error occurred during registration.");
      } else {
        setError("An error occurred during registration.");
      }
    }    
  };

  const handleRegisterSuccess = () => {
    setShowSuccessModal(true);
    setTimeout(() => {
      setShowSuccessModal(false);
      onRequestClose();
      window.location.reload();
    }, 2000);
  };

  return (
    <>
      <Modal className="ReactModal__Overlay" isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Register">
        <form onSubmit={handleSubmit}>
          <div>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          <button type="submit">Register</button>
        </form>
      </Modal>
      {showSuccessModal && (
        <Modal className="ReactModal__Overlay success" isOpen={showSuccessModal} contentLabel="Success">
          <div>
            <p>Registration successful!</p>
          </div>
        </Modal>
      )}
    </>
  );
};

export default RegisterModal;
