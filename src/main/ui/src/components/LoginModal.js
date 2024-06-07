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
      const response = await axios.post("/authenticate", {
        username,
        password,
      });
      if (response.data.jwtToken) {
        localStorage.setItem("token", response.data.jwtToken);
        alert("Login successful!");
        onRequestClose();
        window.location.reload();
      } else {
        setError("Invalid credentials.");
      }
    } catch (err) {
      setError("Wrong username and/or password.");
    }
  };

  return (
    <Modal isOpen={isOpen} onRequestClose={onRequestClose} contentLabel="Login">
      <form onSubmit={handleSubmit}>
        <div>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ width: "100%" }}
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: "100%" }}
          />
        </div>
        {error && <p className="error-message">{error}</p>}
        <button type="submit" style={{ width: "100%" }}>
          Login
        </button>
      </form>
    </Modal>
  );
};

export default LoginModal;
