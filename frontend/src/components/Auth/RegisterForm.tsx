import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
            // Відправляємо дані користувача на сервер для реєстрації
            const response = await api.post('/users', {
                email: formData.email,
                fullName: formData.fullName,
                password: formData.password
            });

            if (response.status === 201) {
                // Перенаправляємо на сторінку входу після успішної реєстрації
                navigate('/login?registered=true');
            } else {
                setError('Щось пішло не так при реєстрації.');
            }
        } catch (err: any) {
            setError(err.response?.data?.error || 'Помилка реєстрації. Спробуйте ще раз.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-form-container">
            <h2>Створити обліковий запис</h2>
            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="fullName">Повне ім'я</label>
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
                    <label htmlFor="password">Пароль</label>
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
                    {isLoading ? 'Реєстрація...' : 'Зареєструватися'}
                </button>
            </form>

            <div className="auth-footer">
                Вже маєте обліковий запис? <a href="/login">Увійти</a>
            </div>
        </div>
    );
};

export default RegisterForm;