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
      setError("An error occurred during registration.");
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Register">
      <button className="close-button" onClick={onRequestClose}>X</button>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
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
