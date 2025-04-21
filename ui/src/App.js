import AvailableProjects from "./project/components/AvailableProjects";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import MainLayout from "./common/components/MainLayout";
import Login from "./auth/pages/Login";
import Register from "./auth/pages/Register";
import {AuthProvider} from "./common/contexts/AuthContext";
import PublicRoute from "./routes/PublicRoute";
import ProjectLayout from "./project/components/ProjectLayout";
import ProjectTasks from "./project/components/ProjectTasks";
import ProjectMembers from "./project/components/ProjectMembers";
import ProjectOverview from "./project/components/ProjectOverview";
import ProtectedRoute from "./routes/ProtectedRoute";
import ProjectContextLayout from "./project/components/ProjectContextLayout";
import Task from "./project/components/Task";

function App() {

    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<PublicRoute children={<Login />} />} />
                    <Route path="/register" element={<PublicRoute children={<Register />} />} />
                    <Route path="/" element={<ProtectedRoute children={<MainLayout/>}/>}>
                        <Route path="/projects" element={<AvailableProjects />}/>
                        <Route element={<ProjectContextLayout/>}>
                            <Route path="/projects/:projectId" element={<ProjectLayout/>}>
                                <Route path="/projects/:projectId/overview" element={<ProjectOverview/>}/>
                                <Route path="/projects/:projectId" element={<ProjectOverview/>}/>
                                <Route path="/projects/:projectId/tasks" element={<ProjectTasks/>}/>
                                <Route path="/projects/:projectId/members" element={<ProjectMembers/>}/>
                            </Route>
                            <Route path="/projects/:projectId/tasks/:taskId" element={<Task/>}/>
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
