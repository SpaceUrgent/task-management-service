import Projects from "./projectmanagement/components/Projects";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import MainLayout from "./common/components/MainLayout";
import Login from "./auth/pages/Login";
import Register from "./auth/pages/Register";
import {AuthProvider} from "./common/contexts/AuthContext";
import PublicRoute from "./routes/PublicRoute";
import ProjectLayout from "./projectmanagement/components/ProjectLayout";
import ProjectTasks from "./projectmanagement/components/ProjectTasks";
import ProjectMembers from "./projectmanagement/components/ProjectMembers";
import ProjectOverview from "./projectmanagement/components/ProjectOverview";
import ProtectedRoute from "./routes/ProtectedRoute";
import ProjectContextLayout from "./projectmanagement/components/ProjectContextLayout";
import Task from "./projectmanagement/components/Task";

function App() {

    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<PublicRoute children={<Login />} />} />
                    <Route path="/register" element={<PublicRoute children={<Register />} />} />
                    <Route path="/" element={<ProtectedRoute children={<MainLayout/>}/>}>
                        <Route path="/projects" element={<Projects />}/>
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
