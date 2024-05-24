import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./DetailedWeather.css";
import 'bootstrap/dist/css/bootstrap.min.css';

const DetailedWeather = () => {
  const { id } = useParams();
  const [forecastData, setForecastData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchForecastData = async () => {
      try {
        const response = await axios.get(`/forecast/${id}`);
        setForecastData(response.data);
      } catch (err) {
        setError(err.response?.data?.error || "Error fetching forecast data");
      }
    };

    fetchForecastData();
  }, [id]);

  const groupByDate = (data) => {
    const grouped = data.reduce((acc, item) => {
      const date = item.time.split(" ")[0];
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

  const groupedData = groupByDate(forecastData.forecast);

  return (
    <div className="container mt-4">
      <h1 className="text-center">{forecastData.city} - 5 Day Forecast</h1>
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
              {["02:00", "05:00", "08:00", "11:00", "14:00", "17:00", "20:00", "23:00"].map((time) => {
                const forecast = groupedData[date].find(f => f.time.includes(time));
                return (
                  <td key={time}>
                    {forecast ? (
                      <>
                        <div>{forecast.temperature}°C</div>
                        <div>{forecast.description}</div>
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

export default DetailedWeather;
