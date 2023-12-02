// Filename - App.js

import React from "react";
import {
	BrowserRouter as Router,
	Routes,
	Route,
} from "react-router-dom";

import Home from "../pages/HomePage";
import History from "../pages/HistoryPage";

function App() {
	return (
		<Router>
			<Routes>
				<Route exact path="/" element={<Home />} />
				<Route path="/History" element={<History />} />
			</Routes>
		</Router>
	);
}

export default App;
