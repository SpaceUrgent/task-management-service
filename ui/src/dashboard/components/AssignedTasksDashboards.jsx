import React, { useEffect, useState } from 'react';
import { DashboardClient } from '../api/DashboardClient.ts';
import TaskSummary from './TaskSummary';
import TaskList from './TaskList';

export default function AssignedTasksDashboards() {
    const dashboardClient = DashboardClient.getInstance();
    const [summary, setSummary] = useState(null);
    const [overdueTasks, setOverdueTasks] = useState([]);
    const [allTasks, setAllTasks] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                const [summaryData, overdueData, tasksData] = await Promise.all([
                    dashboardClient.getAssignedTaskSummary(),
                    dashboardClient.getOverdueAssignedTasks({ page: 0, size: 5 }),
                    dashboardClient.getAssignedTasks({ page: 0, size: 10 })
                ]);

                setSummary(summaryData);
                setOverdueTasks(overdueData.data);
                setAllTasks(tasksData.data);
            } catch (error) {
                console.error('Error fetching dashboard data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchInitialData();
    }, []);

    const handleLoadMoreOverdue = async (nextPage) => {
        try {
            const response = await DashboardClient.getOverdueAssignedTasks({
                page: nextPage,
                size: 5
            });
            setOverdueTasks(prev => [...prev, ...response.content]);
        } catch (error) {
            console.error('Error loading more overdue tasks:', error);
        }
    };

    const handleLoadMoreTasks = async (nextPage) => {
        try {
            const response = await DashboardClient.getAssignedTasks({
                page: nextPage,
                size: 10
            });
            setAllTasks(prev => [...prev, ...response.content]);
        } catch (error) {
            console.error('Error loading more tasks:', error);
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div>
            {summary && <TaskSummary summary={summary} />}
            
            <TaskList
                title="Overdue Tasks"
                tasks={overdueTasks}
                pageSize={5}
                onLoadMore={handleLoadMoreOverdue}
                initiallyExpanded={true}
            />

            <TaskList
                title="All Assigned Tasks"
                tasks={allTasks}
                pageSize={10}
                onLoadMore={handleLoadMoreTasks}
                initiallyExpanded={true}
            />
        </div>
    );
} 