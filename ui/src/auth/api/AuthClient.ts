import axios, {AxiosInstance} from "axios";
// @ts-ignore
import ApiConstants from "../../ApiConstants.ts";
import Configuration from "../../common/Configuration"

export class AuthClient {
    private static instance: AuthClient;
    private axiosInstance: AxiosInstance;

    private constructor() {
        this.axiosInstance = axios.create({ baseURL: Configuration.apiBaseUrl });
        this.axiosInstance.interceptors.response.use(
            res => res,
            error => {
                const status = error.response?.status;
                const message = error.response?.data?.message;
                if (status >= 400 && status < 500) {
                    throw new AuthError(message);
                }
                throw new ServerError();
            }
        )
    }

    public static getInstance(): AuthClient {
        if (!AuthClient.instance) {
            AuthClient.instance = new AuthClient();
        }
        return AuthClient.instance;
    }

    async registerUser(request: RegisterRequest): Promise<void> {
        const urlEncodedRequest = this.toUrlEncodedRequest(request);
        await this.axiosInstance.post(
            `/users/register`,
            urlEncodedRequest,
            {
                headers : { "Content-Type": "application/x-www-form-urlencoded" },
            }
        );
    }

    async login(request: LoginRequest): Promise<void> {
        const urlEncodedRequest = this.toUrlEncodedRequest(request);
        await this.axiosInstance.post(
            `/auth/login`,
            urlEncodedRequest,
            {
                withCredentials: true,
                headers : { "Content-Type": "application/x-www-form-urlencoded" }
            }
        );
    }

    private toUrlEncodedRequest(request: Object): string {
        const urlEncodedParams =  new URLSearchParams();
        for (const key in request) {
            urlEncodedParams.append(key, request[key]);
        }
        return urlEncodedParams.toString();
    }
}

interface RegisterRequest {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
}

interface LoginRequest {
    email: string;
    password: string;
}


export class AuthError extends Error {

    constructor(message: string) {
        super(message);
    }
}

export class ServerError extends Error {

}