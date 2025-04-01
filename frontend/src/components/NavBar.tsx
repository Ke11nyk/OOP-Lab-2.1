import { Link } from 'react-router-dom';
import './NavBar.css';

const Navbar = () => {
    const isAuthenticated = !!localStorage.getItem('authToken');

    return (
        <nav className="navbar">
            <Link to="/" className="logo">LowCost Airlines</Link>
            <div className="nav-links">
                <Link to="/flights">Flights</Link>
                {isAuthenticated ? (
                    <>
                        <Link to="/profile">Profile</Link>
                        <button onClick={() => {
                            localStorage.removeItem('authToken');
                            window.location.reload();
                        }}>Logout</button>
                    </>
                ) : (
                    <Link to="/login">Login</Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;