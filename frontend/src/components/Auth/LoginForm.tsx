import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/client';
import './AuthForms.css';

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

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            // Використовуємо правильний ендпоінт для автентифікації
            const response = await api.post('/auth/login', {
                email: formData.email,
                password: formData.password
            });

            if (response.data && response.data.id) {
                localStorage.setItem('userId', response.data.id);
                // Можна також зберегти інші дані користувача, якщо потрібно
                // localStorage.setItem('userEmail', response.data.email);

                // Перенаправляємо на сторінку польотів після успішного входу
                navigate('/flights');
            } else {
                setError('Вхід не вдався. Спробуйте ще раз.');
            }
        } catch (err: any) {
            setError(err.response?.data?.error || 'Невірна електронна пошта або пароль');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-form-container">
            <h2>Увійти до облікового запису</h2>
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
                    <label htmlFor="password">Пароль</label>
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
                    {isLoading ? 'Вхід...' : 'Увійти'}
                </button>
            </form>

            <div className="auth-footer">
                Немає облікового запису? <a href="/register">Зареєструватися</a>
            </div>
        </div>
    );
};

export default LoginForm;