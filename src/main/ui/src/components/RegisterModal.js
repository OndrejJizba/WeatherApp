import React, { useState } from "react";
import Modal from "react-modal";
import axios from "axios";
import "./ModalStyles.css";

const RegisterModal = ({ isOpen, onRequestClose }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post("/registration", { username, password });
      if (response.data.error) {
        setError(response.data.error);
      } else {
        alert("Registration successful!");
        onRequestClose();
      }
    } catch (err) {
      if (err.response && err.response.data) {
        setError(err.response.data.error || err.response.data.username || err.response.data.password || "An error occurred during registration.");
      } else {
        setError("An error occurred during registration.");
      }
    }    
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Register">
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
  );
};

export default RegisterModal;
