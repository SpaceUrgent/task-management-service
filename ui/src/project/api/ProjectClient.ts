// @ts-ignore
import ApiConstants from "../../ApiConstants.ts";
import axios, {AxiosInstance, AxiosResponse} from "axios";

export class ProjectClient {
    private static instance: ProjectClient;
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
                }
                return Promise.resolve(error);
            }
        )
    }

    public static getInstance(): ProjectClient {
        if (!ProjectClient.instance) {
            this.instance = new ProjectClient(ApiConstants.BASE_URL);
        }
        return ProjectClient.instance;
    }

    setOnUnauthorized(onUnauthorized: Function): void {
        this.onUnauthorized = onUnauthorized;
    }

    async createProject(request : CreateProjectRequest): Promise<void> {
        const response = await this.axiosInstance.post('/projects', request);
        if (response.status === 201) return;
        this.validateApiResponse(response);
    }

    async getAvailableProjects(): Promise<ProjectPreview[]> {
        const response = await this.axiosInstance.get<ApiResponse<ProjectPreview[]>>('/projects');
        console.log('response', response);
        if (response.status === 200) {
            return response.data.data;
        }
        this.validateApiResponse(response);
    }

    async getProjectDetails(projectId: number): Promise<ProjectDetails> {
        const response = await this.axiosInstance.get<ProjectDetails>(`/projects/${projectId}`);
        if (response.status === 200) {
            return response.data;
        }
        this.validateApiResponse(response);
    }

    async getProjectMembers(projectId: number): Promise<User[]> {
        const response = await this.axiosInstance.get<ApiResponse<User[]>>(`/projects/${projectId}/members`);
        if (response.status === 200) {
            return response.data?.data;
        }
        this.validateApiResponse(response);
    }

    async addProjectMember(projectId: number, memberEmail: string): Promise<void> {
        const request = {email: memberEmail};
        const response = await this.axiosInstance.post(`/projects/${projectId}/members`, request);
        if (response.status === 200) {
            return;
        }
        this.validateApiResponse(response);
    }

    async createTask(projectId: number, newTask: CreateTaskRequest): Promise<void> {
        const response = await this.axiosInstance.post(`/projects/${projectId}/tasks`, newTask);
        if (response.status === 201) return;
        this.validateApiResponse(response);
    }

    async getTaskPreviews(projectId: number, options?: GetTaskPreviewsOptions): Promise<Page<TaskPreview>> {
        const response = await this.axiosInstance.get<Page<TaskPreview>>(
            `/projects/${projectId}/tasks?${this.buildQueryParams(options)}`,
        );
        if (response.status === 200) {
            return response.data;
        }
        this.validateApiResponse(response);
    }

    async getTaskDetails(taskId: number): Promise<TaskDetails> {
        const response = await this.axiosInstance.get(`/tasks/${taskId}`);
        if (response.status === 200) {
            return response.data;
        }
        this.validateApiResponse(response);
    }

    async updateTask(taskId: number, request: UpdateTaskRequest): Promise<void> {
        const response = await this.axiosInstance.put(`/tasks/${taskId}`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    async updateTaskStatus(taskId: number, status: string): Promise<void> {
        const request = {status: status};
        const response = await this.axiosInstance.patch(`/tasks/${taskId}/status`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    async assignTask(taskId: number, assigneeId: number): Promise<void> {
        const request = {assigneeId: assigneeId};
        const response = await this.axiosInstance.patch(`/tasks/${taskId}/assign`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    private buildQueryParams(options: GetTaskPreviewsOptions): string {
        if (!options) return '';
        const params = new URLSearchParams();
        Object.entries(options).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== "") {
                params.append(key, value.toString());
            }
        })
        return params.toString();
    }

    private validateApiResponse(response: AxiosResponse<any, any>) {
        const statusCode = response.status;
        if (statusCode === 401 || statusCode === 403) {
            console.log('handle unauthorized response');
            this.onUnauthorized();
            return;
        }
        if (statusCode === 400 || statusCode === 404) {
            throw new ProjectClientError(response.data?.message);
        }
        throw new ServerError();
    }
}

interface ApiResponse<T> {
    data: T;
}

interface Page<T> extends ApiResponse<T[]> {
    currentPage: number;
    pageSize: number;
    total: number;
    totalPages: number;
}

interface CreateProjectRequest {
    title: string;
    description: string;
}

interface CreateTaskRequest {
    title: string;
    description: string;
    assigneeId: number;
}

interface UpdateTaskRequest {
    title: string;
    description: string;
    assigneeId: number;
    status: string;
}

interface UpdateTaskStatusRequest {
    status: string;
}

interface AssignTaskRequest {
    assigneeId: number;
}

interface ProjectPreview {
    id: number;
    title: string;
    owner: User;
}

interface TaskPreview {
    id: number;
    createdAt: Date;
    title: string;
    status: string;
    assignee: User;
}

interface TaskDetails {
    id: number;
    createdAt: Date;
    updatedAt: Date;
    projectId: number;
    title: string;
    description: string;
    status: string;
    assignee: User;
    owner: User;
}

interface ProjectDetails {
    id: number;
    createdAt: Date;
    updatedAt: Date;
    title: string;
    description: string;
    owner: User;
    taskStatuses: string[];
    members: User[];
}

interface User {
    id: number;
    email: string;
    fullName: string;
}

interface GetTaskPreviewsOptions {
    page?: number;
    size?: number;
    assigneeId?: number;
    status?: string;
    sortBy?: string;
}

export class ProjectClientError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export class ServerError extends Error {
}