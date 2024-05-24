import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import axios from "axios";
import WeatherList from "./components/WeatherList";
import DetailedWeather from "./components/DetailedWeather";

const App = () => {
  const [weatherData, setWeatherData] = useState([]);

  useEffect(() => {
    const fetchWeatherData = async () => {
      const response = await axios.get("/cities");
      setWeatherData(response.data);
    };

    fetchWeatherData();
  }, []);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<WeatherList />} />
        <Route path="/weather/:id" element={<DetailedWeather />} />
      </Routes>
    </Router>
  );
};

export default App;
