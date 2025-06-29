import React, { useState } from 'react';
import AssignedTasksDashboards from '../components/AssignedTasksDashboards';
import OwnedTasksDashboards from '../components/OwnedTasksDashboards';

export default function DashboardLayout() {
    const [activeView, setActiveView] = useState('assigned');

    return (
        <div className="container-fluid h-100 d-flex flex-column p-0">
            <div className="d-flex align-items-center w-100 mb-1">
                <h5 className="ms-2 mb-0">Dashboard</h5>
            </div>

            <div className="card mb-4 border-0">
                <div className="card-header bg-white">
                    <ul className="nav nav-tabs card-header-tabs">
                        <li className="nav-item">
                            <button 
                                className={`nav-link ${activeView === 'assigned' ? 'active' : ''}`}
                                onClick={() => setActiveView('assigned')}
                            >
                                Assigned to Me
                            </button>
                        </li>
                        <li className="nav-item">
                            <button 
                                className={`nav-link ${activeView === 'owned' ? 'active' : ''}`}
                                onClick={() => setActiveView('owned')}
                            >
                                Owned by Me
                            </button>
                        </li>
                    </ul>
                </div>
                <div className="card-body">
                    {activeView === 'assigned' ? <AssignedTasksDashboards /> : <OwnedTasksDashboards />}
                </div>
            </div>
        </div>
    );
} 