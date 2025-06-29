class Configuration {

    static get apiBaseUrl() {
        return process.env.REACT_APP_API_URL;
    }
}

export default Configuration;
