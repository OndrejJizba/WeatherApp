import React from "react";
import Weather from "./components/Weather";

const App = () => {
  return (
    <div className="App">
      <Weather cityId={2} />
    </div>
  );
};

export default App;
