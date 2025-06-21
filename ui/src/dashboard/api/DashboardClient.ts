// @ts-ignore
import ApiConstants from "../../ApiConstants.ts";
import axios, {AxiosInstance} from "axios";
import Configuration from "../../common/Configuration.js"

export class DashboardClient {
    private static instance: DashboardClient;
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
                    throw new DashboardClientError(message);
                }
                throw new ServerError();
            }
        )
    }

    public static getInstance(): DashboardClient {
        if (!DashboardClient.instance) {
            this.instance = new DashboardClient(Configuration.apiBaseUrl);
        }
        return DashboardClient.instance;
    }

    setOnUnauthorized(onUnauthorized: Function): void {
        this.onUnauthorized = onUnauthorized;
    }

    async getAssignedTaskSummary(): Promise<TaskSummary> {
        const response = await this.axiosInstance.get<TaskSummary>('/dashboard/tasks/assigned/summary');
        return response.data;
    }

    async getAssignedTasks(page: number = 1, size: number = 10): Promise<Page<DashboardTaskPreview>> {
        const response = await this.axiosInstance.get<Page<DashboardTaskPreview>>('/dashboard/tasks/assigned');
        return response.data;
    }

    async getOverdueAssignedTasks(page: number = 1, size: number = 5): Promise<Page<DashboardTaskPreview>> {
        const response = await this.axiosInstance.get<Page<DashboardTaskPreview>>('/dashboard/tasks/assigned/overdue');
        return response.data;
    }

    async getOwnedTaskSummary(): Promise<TaskSummary> {
        const response = await this.axiosInstance.get<TaskSummary>('/dashboard/tasks/owned/summary');
        return response.data;
    }

    async getOwnedTasks(page: number = 1, size: number = 10): Promise<Page<DashboardTaskPreview>> {
        const response = await this.axiosInstance.get<Page<DashboardTaskPreview>>('/dashboard/tasks/owned');
        return response.data;
    }

    async getOverdueOwnedTasks(page: number = 1, size: number = 5): Promise<Page<DashboardTaskPreview>> {
        const response = await this.axiosInstance.get<Page<DashboardTaskPreview>>('/dashboard/tasks/owned/overdue');
        return response.data;
    }
}

interface TaskSummary {
    total: number;
    open: number;
    overdue: number;
    closed: number;
}

interface DashboardTaskPreview {
    createdAt: Date;
    taskId: number;
    number: number;
    title: string;
    projectId: number;
    projectTitle: string;
    dueDate: Date;
    isOverdue: boolean;
    priority: string;
    status: string;
    user: User;
}

interface User {
    id: number;
    email: string;
    fullName: string;
}

interface Page<T> extends ApiResponse<T[]> {
    currentPage: number;
    pageSize: number;
    total: number;
    totalPages: number;
}

interface ApiResponse<T> {
    data: T;
}

export class DashboardClientError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export class ServerError extends Error {
}