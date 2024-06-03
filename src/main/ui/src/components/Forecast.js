import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./DetailedWeather.css";
import "bootstrap/dist/css/bootstrap.min.css";

const Forecast = () => {
  const { lat, lon } = useParams();
  const [forecastData, setForecastData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchForecastData = async () => {
      try {
        const response = await axios.get(`/forecast?lat=${lat}&lon=${lon}`);
        setForecastData(response.data);
      } catch (err) {
        setError(err.response?.data?.error || "Error fetching forecast data");
      }
    };

    fetchForecastData();
  }, [lat, lon]);

  const formatDate = (timestamp) => {
    const date = new Date(timestamp * 1000);
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp * 1000);
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");
    return `${hours}:${minutes}`;
  };

  const groupByDate = (data) => {
    const grouped = data.reduce((acc, item) => {
      const date = formatDate(item.dt);
      if (!acc[date]) {
        acc[date] = [];
      }
      acc[date].push(item);
      return acc;
    }, {});
    return grouped;
  };

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  if (!forecastData) {
    return <div className="loading">Loading...</div>;
  }

  const groupedData = groupByDate(forecastData);

  return (
    <div className="container mt-4">
      <h1 className="text-center">5 Day Forecast</h1>
      <table className="table table-hover table-custom mt-3">
        <thead className="thead-dark">
          <tr>
            <th>Date</th>
            <th>02:00</th>
            <th>05:00</th>
            <th>08:00</th>
            <th>11:00</th>
            <th>14:00</th>
            <th>17:00</th>
            <th>20:00</th>
            <th>23:00</th>
          </tr>
        </thead>
        <tbody>
          {Object.keys(groupedData).map((date) => (
            <tr key={date}>
              <td>{date}</td>
              {[
                "02:00",
                "05:00",
                "08:00",
                "11:00",
                "14:00",
                "17:00",
                "20:00",
                "23:00",
              ].map((time) => {
                const forecast = groupedData[date].find((f) =>
                  formatTime(f.dt).includes(time)
                );
                return (
                  <td key={time}>
                    {forecast ? (
                      <>
                        <div>{Math.round(forecast.temp)}°C</div>
                        <div>
                          <img
                            src={forecast.icon}
                            alt="Weather Icon"
                            className="forecast-icon"
                          />
                        </div>
                      </>
                    ) : (
                      <div></div>
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Forecast;
