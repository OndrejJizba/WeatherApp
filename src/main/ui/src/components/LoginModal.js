import React, { useState } from "react";
import Modal from "react-modal";
import axios from "axios";
import "./ModalStyles.css";

const LoginModal = ({ isOpen, onRequestClose }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post("/authenticate", { username, password });
      if (response.data.jwtToken) {
        localStorage.setItem("token", response.data.jwtToken);
        alert("Login successful!");
        onRequestClose();
        window.location.reload();
      } else {
        setError("Invalid credentials.");
      }
    } catch (err) {
      setError("An error occurred during login.");
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Login">
      <button className="close-button" onClick={onRequestClose}>X</button>
      <h2>Login</h2>
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
        <button type="submit">Login</button>
      </form>
    </Modal>
  );
};

export default LoginModal;
