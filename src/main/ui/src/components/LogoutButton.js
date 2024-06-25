import React from "react";
import { useNavigate } from "react-router-dom";

const LogoutButton = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    navigate("/");
  };

  return (
    <button className="nav-button" onClick={handleLogout}>
      Logout
    </button>
  );
};

export default LogoutButton;