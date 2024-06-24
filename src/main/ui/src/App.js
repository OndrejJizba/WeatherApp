import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Route, Routes, Link } from "react-router-dom";
import axios from "axios";
import WeatherList from "./components/WeatherList";
import DetailedWeather from "./components/DetailedWeather";
import SearchBar from "./components/SearchBar";
import Forecast from "./components/Forecast";
import RegisterModal from "./components/RegisterModal";
import LoginModal from "./components/LoginModal";
import Profile from "./components/Profile";
import "./components/ModalStyles.css";
import "./App.css";

const App = () => {
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const fetchWeatherData = async () => {
      const response = await axios.get("/cities");
    };

    fetchWeatherData();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    window.location.reload();
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
  }
}, []);

  return (
    <Router>
      <div>
        <nav className="nav-container">
          <Link to="/" className="nav-button">Home</Link>
          {!isLoggedIn ? (
            <>
              <button className="nav-button" onClick={() => setIsRegisterModalOpen(true)}>Register</button>
              <button className="nav-button" onClick={() => setIsLoginModalOpen(true)}>Login</button>
            </>
          ) : (
            <>
              <Link to="/profile" className="nav-button">Profile</Link>
              <button className="nav-button" onClick={handleLogout}>Logout</button>
            </>
          )}
        </nav>
        <Routes>
          <Route
            path="/"
            element={
              <>
                <SearchBar />
                <WeatherList />
              </>
            }
          />
          <Route path="/weather/:id" element={<DetailedWeather />} />
          <Route path="/forecast/:lat/:lon" element={<Forecast />} />
          <Route path="/profile" element={<Profile />} />
        </Routes>
        <RegisterModal
          isOpen={isRegisterModalOpen}
          onRequestClose={() => setIsRegisterModalOpen(false)}
        />
        <LoginModal
          isOpen={isLoginModalOpen}
          onRequestClose={() => setIsLoginModalOpen(false)}
        />
      </div>
    </Router>
  );
};

export default App;
