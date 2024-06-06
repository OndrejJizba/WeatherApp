import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import axios from "axios";
import WeatherList from "./components/WeatherList";
import DetailedWeather from "./components/DetailedWeather";
import SearchBar from "./components/SearchBar";
import Forecast from "./components/Forecast";
import RegisterModal from "./components/RegisterModal";
import LoginModal from "./components/LoginModal";
import "./components/ModalStyles.css";

const App = () => {
  const [weatherData, setWeatherData] = useState([]);
  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"));

  useEffect(() => {
    const fetchWeatherData = async () => {
      const response = await axios.get("/cities");
      setWeatherData(response.data);
    };

    fetchWeatherData();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    window.location.reload(); 
  };

  return (
    <Router>
      <div>
        <nav className="nav-container">
          {!isLoggedIn ? (
            <>
              <button
                className="nav-button"
                onClick={() => setIsRegisterModalOpen(true)}
              >
                Register
              </button>
              <button
                className="nav-button"
                onClick={() => setIsLoginModalOpen(true)}
              >
                Login
              </button>
            </>
          ) : (
            <button className="nav-button" onClick={handleLogout}>
              Logout
            </button>
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
