import AvailableProjects from "./project/overview/AvailableProjects";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import MainLayout from "./shared/components/MainLayout";
import Login from "./auth/pages/Login";
import Register from "./auth/pages/Register";
import {AuthProvider} from "./shared/contexts/AuthContext";
import PublicRoute from "./routes/PublicRoute";
import ProjectLayout from "./project/layouts/ProjectLayout";
import ProjectTasks from "./project/tasks/ProjectTasks";
import ProjectMembers from "./project/members/ProjectMembers";
import ProjectProfile from "./project/profile/ProjectProfile";
import ProtectedRoute from "./routes/ProtectedRoute";
import ProjectContextLayout from "./project/layouts/ProjectContextLayout";
import Task from "./project/tasks/Task";
import UserProfile from "./iam/components/UserProfile";
import DashboardLayout from "./dashboard/layouts/DashboardLayout";

function App() {

    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<PublicRoute children={<Login />} />} />
                    <Route path="/register" element={<PublicRoute children={<Register />} />} />
                    <Route path="/" element={<ProtectedRoute children={<MainLayout/>}/>}>
                        <Route path="/" element={<AvailableProjects/>}/>
                        <Route path="/projects" element={<AvailableProjects />}/>
                        <Route path="/dashboard" element={<DashboardLayout />}/>
                        <Route element={<ProjectContextLayout/>}>
                            <Route path="/projects/:projectId" element={<ProjectLayout/>}>
                                <Route path="/projects/:projectId/profile" element={<ProjectProfile/>}/>
                                <Route path="/projects/:projectId" element={<ProjectProfile/>}/>
                                <Route path="/projects/:projectId/tasks" element={<ProjectTasks/>}/>
                                <Route path="/projects/:projectId/members" element={<ProjectMembers/>}/>
                            </Route>
                            <Route path="/projects/:projectId/tasks/:taskId" element={<Task/>}/>
                        </Route>
                        <Route path="/profile" element={<UserProfile/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
