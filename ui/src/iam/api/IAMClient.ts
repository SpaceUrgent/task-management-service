// @ts-ignore
import ApiConstants from "../../ApiConstants.ts";
import axios, {AxiosInstance} from "axios";

export class IAMClient {
    private static instance: IAMClient;
    private axiosInstance: AxiosInstance;
    private onUnauthorized: Function;

    private constructor(baseUrl: string) {
        this.axiosInstance = axios.create({
            baseURL: baseUrl,
            headers: {
                'Content-Type': 'application/json',
            },
            withCredentials: true
        });
        this.axiosInstance.interceptors.response.use(
            res => res,
            error => {
                if ((error.response?.status === 401 || error.response?.status === 403)
                    && this.onUnauthorized) {
                    this.onUnauthorized();
                    return Promise.reject(error);
                }
                const status = error.response?.status;
                const message = error.response?.data?.message;
                console.log('error: ', error.response.data);
                if (status >= 400 && status < 500) {
                    throw new IAMClientError(message);
                }
                throw new ServerError();
            }
        )
    }

    public static getInstance(): IAMClient {
        if (!IAMClient.instance) {
            this.instance = new IAMClient(ApiConstants.BASE_URL);
        }
        return IAMClient.instance;
    }

    setOnUnauthorized(onUnauthorized: Function): void {
        this.onUnauthorized = onUnauthorized;
    }

    async getUserProfile(): Promise<UserProfile> {
        const response = await this.axiosInstance.get<UserProfile>('/users/profile');
        return response.data;
    }

    async updateUserProfile(request: UpdateUserProfileRequest): Promise<void> {
        await this.axiosInstance.put(`/users`, request);
    }

    async updatePassword(request: UpdatePasswordRequest): Promise<void> {
        console.log('request: ', request);
        await this.axiosInstance.post(
            `/users/password`,
            this.toUrlEncodedRequest(request),
            {
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
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

interface UpdateUserProfileRequest {
    firstName: string;
    lastName: string;
}

interface UpdatePasswordRequest {
    currentPassword: string;
    newPassword: string;
}

interface UserProfile {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
}

export class IAMClientError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export class ServerError extends Error {
}