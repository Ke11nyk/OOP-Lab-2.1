import { BrowserRouter, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import FlightsPage from './pages/FlightsPage';
import ProfilePage from './pages/ProfilePage';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import Navbar from './components/NavBar';
import './App.css';
import {AuthProvider} from "./components/Auth/AuthContext";

function App() {
  return (
      <BrowserRouter>
          <AuthProvider>
              <Navbar />
              <div className="app-content">
                  <Routes>
                      <Route path="/" element={<HomePage />} />
                        <Route path="/flights" element={<FlightsPage />} />
                        <Route path="/profile" element={<ProfilePage />} />
                        <Route path="/login" element={<LoginForm />} />
                        <Route path="/register" element={<RegisterForm />} />
                  </Routes>
              </div>
          </AuthProvider>
      </BrowserRouter>
  );
}

export default App;