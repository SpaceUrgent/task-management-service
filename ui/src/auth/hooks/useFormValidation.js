import { useState, useEffect } from 'react';
import AppConstants from '../../AppConstants.ts';

export const useFormValidation = (validationRules) => {
    const [formData, setFormData] = useState({});
    const [validation, setValidation] = useState({});
    const [showErrors, setShowErrors] = useState({});

    useEffect(() => {
        const initialData = {};
        const initialValidation = {};
        const initialShowErrors = {};

        Object.keys(validationRules).forEach(field => {
            initialData[field] = '';
            initialValidation[field] = false;
            initialShowErrors[field] = false;
        });

        setFormData(initialData);
        setValidation(initialValidation);
        setShowErrors(initialShowErrors);
    }, []);

    const updateField = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
        
        const isValid = validationRules[field](value, formData);
        setValidation(prev => ({ ...prev, [field]: isValid }));
    };

    const showFieldError = (field) => {
        setShowErrors(prev => ({ ...prev, [field]: true }));
    };

    const isFormValid = () => {
        return Object.keys(validationRules).every(field => validation[field]);
    };

    const resetForm = () => {
        const initialData = {};
        const initialValidation = {};
        const initialShowErrors = {};

        Object.keys(validationRules).forEach(field => {
            initialData[field] = '';
            initialValidation[field] = false;
            initialShowErrors[field] = false;
        });

        setFormData(initialData);
        setValidation(initialValidation);
        setShowErrors(initialShowErrors);
    };

    return {
        formData,
        validation,
        showErrors,
        updateField,
        showFieldError,
        isFormValid,
        resetForm
    };
};

export const validationRules = {
    email: (value) => value && value.match(AppConstants.VALID_EMAIL_REGEX),
    password: (value) => value && value.length >= 8,
    confirmPassword: (value, formData) => value && value === formData.password,
    firstName: (value) => value && AppConstants.VALID_NAME_REGEX.test(value),
    lastName: (value) => value && AppConstants.VALID_NAME_REGEX.test(value),
    required: (value) => !!value
}; 