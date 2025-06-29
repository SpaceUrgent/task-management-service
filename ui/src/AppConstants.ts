class AppConstants {
    static readonly VALID_EMAIL_REGEX = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
    static readonly VALID_NAME_REGEX = /^[A-Za-z'-]{2,49}$/;
    static readonly DEFAULT_LOGIN_ERROR_MESSAGE = 'Failed to login. Please try again later';
    static readonly DEFAULT_REGISTER_ERROR_MESSAGE = 'Failed to register. Please try again later';
}

export default AppConstants;