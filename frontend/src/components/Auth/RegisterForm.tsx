import { useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import api from '../../api/client';
import './AuthForms.css';

const RegisterForm = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        fullName: ''
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

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const response = await api.post('/users', {
                email: formData.email,
                fullName: formData.fullName,
                password: formData.password
            });

            if (response.status === 201) {
                localStorage.setItem('userId', response.data.id);
                localStorage.setItem('authToken', 'true');
                localStorage.setItem('userName', formData.fullName);

                // Перенаправляємо на головну сторінку
                navigate('/');
            } else {
                setError('Something went wrong during registration.');
            }
        } catch (err: any) {
            setError(err.response?.data?.error || 'Registration error. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-form-container">
            <h2>Create an account</h2>
            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="fullName">Full name</label>
                    <input
                        type="text"
                        id="fullName"
                        name="fullName"
                        value={formData.fullName}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
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
                        minLength={6}
                        required
                    />
                </div>

                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Registration...' : 'Register'}
                </button>
            </form>

            <div className="auth-footer">
                Already have an account? <Link to="/login">Log in</Link>
            </div>
        </div>
    );
};

export default RegisterForm;