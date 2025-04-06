import { Link } from 'react-router-dom';
import './NavBar.css';

const NavBar = () => {
    // Перевіряємо, чи користувач залогінений (наприклад, за наявністю токена)
    const isAuthenticated = !!localStorage.getItem('authToken');
    // Отримуємо ім'я користувача, якщо воно збережене
    const userName = localStorage.getItem('userName');

    const handleLogout = () => {
        // Видаляємо дані користувача з localStorage
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        // Оновлюємо сторінку
        window.location.reload();
    };

    return (
        <nav className="navbar">
            <Link to="/" className="logo">LowCost Airlines</Link>
            <div className="nav-links">
                <Link to="/flights">Flights</Link>

                {isAuthenticated ? (
                    <>
                        {/* Якщо користувач залогінений */}
                        {userName && <span className="user-greeting">Hello, {userName}</span>}
                        <Link to="/profile">Profile</Link>
                        <button onClick={handleLogout} className="logout-button">
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        {/* Якщо користувач не залогінений */}
                        <Link to="/login" className="auth-link">Login</Link>
                        <Link to="/register" className="auth-link">Register</Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default NavBar;