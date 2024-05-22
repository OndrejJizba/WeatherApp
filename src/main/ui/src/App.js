import React from "react";
import Weather from "./components/Weather";
import WeatherList from "./components/WeatherList";

const App = () => {
  return (
    <div className="App">
      {/* <Weather cityId={2} /> */}
      <WeatherList />
    </div>
  );
};

export default App;
