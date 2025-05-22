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
                    return Promise.reject(error);
                }
                const status = error.response?.status;
                const message = error.response?.data?.message;
                console.log('error: ', error.response.data);
                if (status >= 400 && status < 500) {
                    throw new ProjectClientError(message);
                }
                throw new ServerError();
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

    async updateProjectInfo(projectId: number, request : UpdateProjectInfoRequest): Promise<void> {
        const response = await this.axiosInstance.put(`/projects/${projectId}`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    async addTaskStatus(projectId: number, request : AddTaskStatusRequest): Promise<void> {
        const response = await this.axiosInstance.put(`/projects/${projectId}/available-statuses`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    async removeTaskStatus(projectId: number, status: string): Promise<void> {
        const response = await this.axiosInstance.delete(`/projects/${projectId}/available-statuses/${status}`);
        if (response.status === 200) return;
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

    async getProjectDetails(projectId: number): Promise<UserProjectDetails> {
        const response = await this.axiosInstance.get<UserProjectDetails>(`/projects/${projectId}`);
        if (response.status === 200) {
            return response.data;
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

    async addTaskComment(taskId: number, comment: string): Promise<void> {
        const request = {comment: comment};
        const response = await this.axiosInstance.post(`/tasks/${taskId}/comments`, request);
        if (response.status === 200) return;
        this.validateApiResponse(response);
    }

    async updateMemberRole(projectId: number, memberId: number, role: MemberRole): Promise<void> {
        const request = {
            memberId: memberId,
            role: role,
        };
        await this.axiosInstance.put(`/projects/${projectId}/members`, request);
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
            this.onUnauthorized();
            return;
        }
        if (statusCode >= 400 && statusCode < 500) {
            throw new ProjectClientError(response.data?.message);
        }
        throw new ServerError();
    }
}

interface ApiResponse<T> {
    data: T;
}

interface ErrorResponse {
    message: string;
}

interface Page<T> extends ApiResponse<T[]> {
    currentPage: number;
    pageSize: number;
    total: number;
    totalPages: number;
}

interface UpdateProjectInfoRequest {
    title: string;
    description: string;
}

interface AddTaskStatusRequest {
    name: string;
    position: number;
}

interface CreateProjectRequest {
    title: string;
    description: string;
}

interface CreateTaskRequest {
    title: string;
    description: string;
    assigneeId: number;
    priority: string;
    dueDate: string;
}

interface UpdateTaskRequest {
    title: string;
    description: string;
    dueDate: string;
    assigneeId: number;
    priority: string;
    status: string;
}

interface AddCommentRequest {
    comment: string;
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
    dueDate: string;
    priority: string;
    status: string;
    assignee: User;
    owner: User;
    changeLogs: TaskChangeLog[];
    comments: TaskComment[];
}

interface TaskChangeLog {
    occurredAt: string;
    logMessage: string;
    oldValue: string;
    newValue: string;
}

interface TaskComment {
    id: number;
    createdAt: Date;
    author: User;
    content: string;
}

interface UserProjectDetails {
    role: MemberRole;
    projectDetails: ProjectDetails;
}

enum MemberRole {
    Owner, Admin
}

interface ProjectDetails {
    id: number;
    createdAt: Date;
    updatedAt: Date;
    title: string;
    description: string;
    owner: User;
    taskStatuses: AvailableTaskStatus[];
    taskPriorities: TaskPriority[];
    members: User[];
}

interface AvailableTaskStatus {
    name: string;
    position: number;
}

interface TaskPriority {
    name: string;
    order: number;
}

interface User {
    id: number;
    email: string;
    fullName: string;
}

interface UpdateMemberRoleRequest {
    memberId: number;
    role: MemberRole;
}

interface GetTaskPreviewsOptions {
    page?: number;
    size?: number;
    assigneeId?: number;
    status?: string[];
    sortBy?: string;
}

export class ProjectClientError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export class ServerError extends Error {
}