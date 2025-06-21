import { useState, useEffect } from 'react';

export const useFormValidation = (validationRules) => {
    const [formData, setFormData] = useState({});
    const [validation, setValidation] = useState({});
    const [showErrors, setShowErrors] = useState({});

    useEffect(() => {
        const initialData = {};
        const initialValidation = {};
        const initialShowErrors = {};
        Object.keys(validationRules).forEach(field => {
            initialValidation[field] = validationRules[field]('', {});
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
            initialValidation[field] = validationRules[field]('', {});
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