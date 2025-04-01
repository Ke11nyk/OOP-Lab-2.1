import { BrowserRouter, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import FlightsPage from './pages/FlightsPage';
import ProfilePage from './pages/ProfilePage';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import Navbar from './components/NavBar';
import './App.css';

function App() {
  return (
      <BrowserRouter>
        <Navbar />
        <div className="app-content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="${REACT_APP_API_URL}/flights" element={<FlightsPage />} />
            <Route path="${REACT_APP_API_URL}/profile" element={<ProfilePage />} />
            <Route path="${REACT_APP_API_URL}/login" element={<LoginForm />} />
            <Route path="${REACT_APP_API_URL}/register" element={<RegisterForm />} />
          </Routes>
        </div>
      </BrowserRouter>
  );
}

export default App;