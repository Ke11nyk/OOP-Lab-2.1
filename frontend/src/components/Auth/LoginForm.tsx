import { useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import api from '../../api/client';
import './AuthForms.css';
import {useAuth} from "./AuthContext";

const LoginForm = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const { login } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const response = await api.post('/auth/login', {
                email: formData.email,
                password: formData.password
            });

            if (response.data && response.data.id) {
                // Use the context login function instead
                login(response.data.id, response.data.fullName || 'User');

                // Перенаправляємо на головну сторінку
                navigate('/');
            } else {
                setError('Sign in failed. Please try again.');
            }
        } catch (err: any) {
            setError(err.response?.data?.error || 'Incorrect email or password');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-form-container">
            <h2>Log in to your account</h2>
            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        autoComplete="username"
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        autoComplete="current-password"
                        minLength={6}
                    />
                </div>

                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Entry...' : 'Log in'}
                </button>
            </form>

            <div className="auth-footer">
                Don't have an account? <Link to="/register">Register</Link>
            </div>
        </div>
    );
};

export default LoginForm;