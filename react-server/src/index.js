import React from 'react';
import ReactDOM from 'react-dom/client';

// Import the main styles for the application
import './css/index.css';

// Import the function to report web vitals
import reportWebVitals from './reportWebVitals';

// Import Bootstrap styles
import 'bootstrap/dist/css/bootstrap.css';

// Import the main App component
import App from './components/App';

// Create a React root for rendering
const root = ReactDOM.createRoot(document.getElementById('root'));

// Render the main App component to the root
root.render(<App />);

// Report web vitals for performance monitoring
reportWebVitals();