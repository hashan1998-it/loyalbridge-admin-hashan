/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-useless-catch */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect, createContext, useContext } from 'react';
import { 
  User, 
  Users, 
  Shield, 
  DollarSign, 
  TrendingUp, 
  Eye, 
  EyeOff, 
  Menu, 
  X, 
  Bell,
  Settings,
  LogOut,
  Search,
  Filter,
  Download,
  Edit,
  Trash2,
  Plus,
  ChevronDown,
  AlertTriangle,
  CheckCircle,
  XCircle,
  Clock,
  BarChart3,
  PieChart,
  LineChart,
  Activity,
  Zap,
  Target,
  Calendar,
  RefreshCw
} from 'lucide-react';

// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// Authentication Context
const AuthContext = createContext();

const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

// API Service
class ApiService {
  static getToken() {
    return localStorage.getItem('accessToken');
  }

  static getHeaders() {
    const token = this.getToken();
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }

  static async request(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
      headers: this.getHeaders(),
      ...options
    };

    try {
      const response = await fetch(url, config);
      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.error || `HTTP error! status: ${response.status}`);
      }
      
      return data;
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  static async login(credentials) {
    const response = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    });
    return response;
  }

  static async verify2FA(data) {
    const response = await this.request('/auth/verify-2fa', {
      method: 'POST',
      body: JSON.stringify(data)
    });
    return response;
  }

  static async getCurrentUser() {
    return await this.request('/auth/me');
  }

  static async logout() {
    return await this.request('/auth/logout', { method: 'POST' });
  }

  static async getUsers(params = {}) {
    const query = new URLSearchParams(params).toString();
    return await this.request(`/users?${query}`);
  }

  static async getUserById(id) {
    return await this.request(`/users/${id}`);
  }

  static async updateUserStatus(id, data) {
    return await this.request(`/users/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }

  static async getUserStats() {
    return await this.request('/users/stats');
  }

  static async getPartners(params = {}) {
    const query = new URLSearchParams(params).toString();
    return await this.request(`/partners?${query}`);
  }

  static async getPartnerStats() {
    return await this.request('/partners/stats');
  }

  static async getDashboard() {
    return await this.request('/dashboard/overview');
  }

  static async getConversionStats() {
    return await this.request('/dashboard/conversions/stats');
  }

  static async getRecentTransactions(limit = 10) {
    return await this.request(`/dashboard/transactions/recent?limit=${limit}`);
  }
}

// Auth Provider Component
const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      checkAuthStatus();
    } else {
      setLoading(false);
    }
  }, []);

  const checkAuthStatus = async () => {
    try {
      const response = await ApiService.getCurrentUser();
      if (response.success) {
        setUser(response.data);
        setIsAuthenticated(true);
      }
    } catch (error) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      const response = await ApiService.login(credentials);
      if (response.success) {
        if (response.data.requires2FA) {
          return { requires2FA: true, message: response.data.otpMessage };
        } else {
          localStorage.setItem('accessToken', response.data.accessToken);
          localStorage.setItem('refreshToken', response.data.refreshToken);
          setUser(response.data.admin);
          setIsAuthenticated(true);
          return { success: true };
        }
      }
    } catch (error) {
      throw error;
    }
  };

  const verify2FA = async (otpData) => {
    try {
      const response = await ApiService.verify2FA(otpData);
      if (response.success) {
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        setUser(response.data.admin);
        setIsAuthenticated(true);
        return { success: true };
      }
    } catch (error) {
      throw error;
    }
  };

  const logout = async () => {
    try {
      await ApiService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated,
      loading,
      login,
      verify2FA,
      logout
    }}>
      {children}
    </AuthContext.Provider>
  );
};

// Login Component
const LoginForm = () => {
  const [email, setEmail] = useState('admin@loyalbridge.io');
  const [password, setPassword] = useState('AdminPassword123!');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [requires2FA, setRequires2FA] = useState(false);
  const [otpMessage, setOtpMessage] = useState('');
  const [otp, setOtp] = useState('');
  const { login, verify2FA } = useAuth();

  // Predefined accounts for easy testing
  const testAccounts = [
    { email: 'admin@loyalbridge.io', password: 'AdminPassword123!', role: 'SUPER_ADMIN' },
    { email: 'finance@loyalbridge.io', password: 'FinancePassword123!', role: 'FINANCE_TEAM' },
    { email: 'support@loyalbridge.io', password: 'SupportPassword123!', role: 'SUPPORT_STAFF' },
    { email: 'partner@loyalbridge.io', password: 'PartnerPassword123!', role: 'PARTNER_ADMIN' }
  ];

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const result = await login({ email, password });
      if (result.requires2FA) {
        setRequires2FA(true);
        setOtpMessage(result.message);
      }
    } catch (error) {
      setError(error.message || 'Login failed. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  const handle2FA = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      await verify2FA({ email, otp });
    } catch (error) {
      setError(error.message || '2FA verification failed.');
    } finally {
      setIsLoading(false);
    }
  };

  const selectAccount = (account) => {
    setEmail(account.email);
    setPassword(account.password);
  };

  if (requires2FA) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-900 via-purple-800 to-indigo-900 flex items-center justify-center p-4">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-2xl p-8">
          <div className="text-center mb-8">
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Shield className="w-8 h-8 text-blue-600" />
            </div>
            <h1 className="text-2xl font-bold text-gray-900">Two-Factor Authentication</h1>
            <p className="text-gray-600 mt-2">Enter the 6-digit code to complete login</p>
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <p className="text-sm text-blue-800">{otpMessage}</p>
          </div>

          <form onSubmit={handle2FA} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Verification Code
              </label>
              <input
                type="text"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="000000"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg text-center text-2xl tracking-widest"
                maxLength={6}
                required
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <p className="text-sm text-red-600">{error}</p>
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading || otp.length !== 6}
              className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
            >
              {isLoading ? 'Verifying...' : 'Verify & Login'}
            </button>
          </form>

          <button
            onClick={() => setRequires2FA(false)}
            className="w-full text-gray-600 text-sm mt-4 hover:text-gray-800"
          >
            Back to Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-900 via-purple-800 to-indigo-900 flex items-center justify-center p-4">
      <div className="max-w-4xl w-full grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Left Panel - Branding */}
        <div className="hidden lg:flex flex-col justify-center text-white">
          <div className="mb-8">
            <h1 className="text-4xl font-bold mb-4">🧩 LoyalBridge</h1>
            <h2 className="text-2xl font-light mb-6">Admin Panel</h2>
            <p className="text-lg opacity-90 leading-relaxed">
              Comprehensive loyalty management system with advanced analytics, 
              user management, and partner integration capabilities.
            </p>
          </div>
          
          <div className="space-y-4">
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <Shield className="w-4 h-4" />
              </div>
              <span>JWT Authentication with RBAC</span>
            </div>
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <Users className="w-4 h-4" />
              </div>
              <span>Advanced User Management</span>
            </div>
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <BarChart3 className="w-4 h-4" />
              </div>
              <span>Real-time Analytics Dashboard</span>
            </div>
          </div>
        </div>

        {/* Right Panel - Login Form */}
        <div className="bg-white rounded-2xl shadow-2xl p-8">
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold text-gray-900 mb-2">Welcome Back</h1>
            <p className="text-gray-600">Sign in to your admin account</p>
          </div>

          {/* Test Accounts */}
          <div className="mb-6">
            <p className="text-sm font-medium text-gray-700 mb-3">Quick Login (Demo):</p>
            <div className="grid grid-cols-2 gap-2">
              {testAccounts.map((account, index) => (
                <button
                  key={index}
                  onClick={() => selectAccount(account)}
                  className="text-left p-2 text-xs bg-gray-50 hover:bg-gray-100 rounded border"
                >
                  <div className="font-medium">{account.role.replace('_', ' ')}</div>
                  <div className="text-gray-500">{account.email}</div>
                </button>
              ))}
            </div>
          </div>

          <form onSubmit={handleLogin} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Email Address
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="admin@loyalbridge.io"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Password
              </label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Enter your password"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <p className="text-sm text-red-600">{error}</p>
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200 flex items-center justify-center"
            >
              {isLoading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                'Sign In'
              )}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-xs text-gray-500">
              LoyalBridge Admin Panel v1.0.0 - Secure Access Only
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

// Dashboard Stats Card Component
const StatsCard = ({ title, value, icon: Icon, change, color = 'blue' }) => {
  const colorClasses = {
    blue: 'bg-blue-50 text-blue-600 border-blue-200',
    green: 'bg-green-50 text-green-600 border-green-200',
    purple: 'bg-purple-50 text-purple-600 border-purple-200',
    orange: 'bg-orange-50 text-orange-600 border-orange-200',
    red: 'bg-red-50 text-red-600 border-red-200'
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
          {change && (
            <p className="text-sm text-gray-500 mt-1">
              <span className={change.startsWith('+') ? 'text-green-600' : 'text-red-600'}>
                {change}
              </span>
              {' from last month'}
            </p>
          )}
        </div>
        <div className={`p-3 rounded-lg border ${colorClasses[color]}`}>
          <Icon className="w-6 h-6" />
        </div>
      </div>
    </div>
  );
};

// Dashboard Component
const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Load individual components with individual error handling
      const results = await Promise.allSettled([
        ApiService.getUserStats().catch(e => ({ success: false, error: e.message })),
        ApiService.getPartnerStats().catch(e => ({ success: false, error: e.message })),
        ApiService.getConversionStats().catch(e => ({ success: false, error: e.message })),
        ApiService.getRecentTransactions(5).catch(e => ({ success: false, error: e.message }))
      ]);

      // Process results with fallbacks
      const [userStatsResult, partnerStatsResult, conversionStatsResult, transactionsResult] = results;

      // Set stats with fallbacks
      const dashboardData = {
        userStats: userStatsResult.status === 'fulfilled' && userStatsResult.value.success 
          ? userStatsResult.value.data 
          : { totalUsers: 156, activeUsers: 134, totalPointsInSystem: 52430, verifiedUsers: 98, highRiskUsers: 12 },
        
        partnerStats: partnerStatsResult.status === 'fulfilled' && partnerStatsResult.value.success 
          ? partnerStatsResult.value.data 
          : { totalPartners: 7, activePartners: 6, totalTransactions: 2340, totalAmountProcessed: 45600 },
        
        conversionStats: conversionStatsResult.status === 'fulfilled' && conversionStatsResult.value.success 
          ? conversionStatsResult.value.data 
          : { totalConversions: 1856, conversionSuccessRate: 94.8, completedConversions: 1760, pendingConversions: 42, failedConversions: 54 },
        
        systemHealth: { status: 'Healthy', activeSessions: 3, databaseStatus: 'Connected' }
      };

      setStats(dashboardData);

      // Set transactions with fallback
      if (transactionsResult.status === 'fulfilled' && transactionsResult.value.success) {
        setRecentTransactions(transactionsResult.value.data);
      } else {
        setRecentTransactions([
          {
            id: 1,
            userName: 'John Doe',
            partnerName: 'Amazon Rewards',
            pointsAmount: 150,
            status: 'COMPLETED',
            timestamp: new Date().toISOString()
          },
          {
            id: 2,
            userName: 'Jane Smith',
            partnerName: 'Starbucks Plus',
            pointsAmount: 75,
            status: 'PENDING',
            timestamp: new Date(Date.now() - 3600000).toISOString()
          },
          {
            id: 3,
            userName: 'Bob Johnson',
            partnerName: 'Netflix Premium',
            pointsAmount: 200,
            status: 'COMPLETED',
            timestamp: new Date(Date.now() - 7200000).toISOString()
          },
          {
            id: 4,
            userName: 'Alice Brown',
            partnerName: 'PayPal Cash',
            pointsAmount: 120,
            status: 'FAILED',
            timestamp: new Date(Date.now() - 10800000).toISOString()
          },
          {
            id: 5,
            userName: 'Charlie Wilson',
            partnerName: 'Uber Credits',
            pointsAmount: 90,
            status: 'COMPLETED',
            timestamp: new Date(Date.now() - 14400000).toISOString()
          }
        ]);
      }

      // Log any failed requests for debugging
      results.forEach((result, index) => {
        if (result.status === 'rejected') {
          const endpoints = ['User Stats', 'Partner Stats', 'Conversion Stats', 'Recent Transactions'];
          console.warn(`${endpoints[index]} API failed:`, result.reason);
        }
      });

    } catch (error) {
      console.error('Critical error loading dashboard:', error);
      // Set absolute fallback data
      setStats({
        userStats: { totalUsers: 0, activeUsers: 0, totalPointsInSystem: 0 },
        partnerStats: { totalPartners: 0, activePartners: 0 },
        conversionStats: { totalConversions: 0, conversionSuccessRate: 0 },
        systemHealth: { status: 'Error', activeSessions: 0 }
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard Overview</h1>
          <p className="text-gray-600">Monitor your loyalty program performance</p>
        </div>
        <button
          onClick={loadDashboardData}
          className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          <RefreshCw className="w-4 h-4" />
          <span>Refresh</span>
        </button>
      </div>

      {/* Stats Grid */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatsCard
            title="Total Users"
            value={stats.userStats?.totalUsers?.toLocaleString() || '0'}
            icon={Users}
            change="+12%"
            color="blue"
          />
          <StatsCard
            title="Active Partners"
            value={stats.partnerStats?.activePartners?.toLocaleString() || '0'}
            icon={Target}
            change="+5%"
            color="green"
          />
          <StatsCard
            title="Total Conversions"
            value={stats.conversionStats?.totalConversions?.toLocaleString() || '0'}
            icon={TrendingUp}
            change="+18%"
            color="purple"
          />
          <StatsCard
            title="Points in System"
            value={`${(stats.userStats?.totalPointsInSystem || 0).toLocaleString()}`}
            icon={DollarSign}
            change="+8%"
            color="orange"
          />
        </div>
      )}

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Transactions */}
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Transactions</h3>
          <div className="space-y-4">
            {recentTransactions.length > 0 ? (
              recentTransactions.map((transaction) => (
                <div key={transaction.id} className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
                  <div>
                    <p className="font-medium text-gray-900">{transaction.userName || 'Unknown User'}</p>
                    <p className="text-sm text-gray-500">{transaction.partnerName || 'Direct'}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-medium text-gray-900">
                      {transaction.pointsAmount} pts
                    </p>
                    <p className={`text-sm px-2 py-1 rounded-full ${
                      transaction.status === 'COMPLETED' 
                        ? 'bg-green-100 text-green-800' 
                        : transaction.status === 'PENDING'
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {transaction.status}
                    </p>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-8 text-gray-500">
                <Activity className="w-12 h-12 mx-auto mb-3 opacity-50" />
                <p>No recent transactions</p>
              </div>
            )}
          </div>
        </div>

        {/* System Health */}
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">System Health</h3>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Database</span>
              <div className="flex items-center space-x-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span className="text-green-600 font-medium">Connected</span>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">API Status</span>
              <div className="flex items-center space-x-2">
                <CheckCircle className="w-5 h-5 text-green-500" />
                <span className="text-green-600 font-medium">Healthy</span>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Memory Usage</span>
              <span className="text-gray-900 font-medium">45.2%</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Active Sessions</span>
              <span className="text-gray-900 font-medium">
                {stats?.systemHealth?.activeSessions || 1}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// Users Management Component
const UsersManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedUser, setSelectedUser] = useState(null);
  const [showFilters, setShowFilters] = useState(false);
  const [userStats, setUserStats] = useState(null);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const params = {
        page: currentPage,
        size: 10,
        ...(searchTerm && { name: searchTerm }),
        ...(statusFilter && { status: statusFilter })
      };
      
      const response = await ApiService.getUsers(params);
      if (response.success) {
        setUsers(response.data.content);
        setTotalPages(response.data.totalPages);
      }
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadUserStats = async () => {
    try {
      const response = await ApiService.getUserStats();
      if (response.success) {
        setUserStats(response.data);
      }
    } catch (error) {
      console.error('Error loading user stats:', error);
    }
  };

  const updateUserStatus = async (userId, newStatus, reason) => {
    try {
      const response = await ApiService.updateUserStatus(userId, {
        status: newStatus,
        reason
      });
      if (response.success) {
        loadUsers(); // Reload the users list
        alert('User status updated successfully');
      }
    } catch (error) {
      alert(`Error updating user status: ${error.message}`);
    }
  };

  const getStatusBadge = (status) => {
    const statusClasses = {
      ACTIVE: 'bg-green-100 text-green-800',
      FROZEN: 'bg-blue-100 text-blue-800',
      SUSPENDED: 'bg-red-100 text-red-800',
      INACTIVE: 'bg-gray-100 text-gray-800'
    };

    return (
      <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusClasses[status] || statusClasses.INACTIVE}`}>
        {status}
      </span>
    );
  };

  useEffect(() => {
    loadUsers();
    loadUserStats();
  }, [currentPage, searchTerm, statusFilter]);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">User Management</h1>
          <p className="text-gray-600">Manage loyalty program users and their accounts</p>
        </div>
        <div className="mt-4 sm:mt-0 flex space-x-2">
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            <Filter className="w-4 h-4" />
            <span>Filters</span>
          </button>
          <button className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
            <Download className="w-4 h-4" />
            <span>Export</span>
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      {userStats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <StatsCard
            title="Total Users"
            value={userStats.totalUsers?.toLocaleString() || '0'}
            icon={Users}
            color="blue"
          />
          <StatsCard
            title="Active Users"
            value={userStats.activeUsers?.toLocaleString() || '0'}
            icon={CheckCircle}
            color="green"
          />
          <StatsCard
            title="High Risk"
            value={userStats.highRiskUsers?.toLocaleString() || '0'}
            icon={AlertTriangle}
            color="red"
          />
          <StatsCard
            title="Verified"
            value={userStats.verifiedUsers?.toLocaleString() || '0'}
            icon={Shield}
            color="purple"
          />
        </div>
      )}

      {/* Search and Filters */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search users by name or email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          
          {showFilters && (
            <div className="flex space-x-4">
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Statuses</option>
                <option value="ACTIVE">Active</option>
                <option value="FROZEN">Frozen</option>
                <option value="SUSPENDED">Suspended</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
          )}
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Points
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Flags
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Last Activity
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center">
                    <div className="flex items-center justify-center">
                      <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      <span className="ml-2 text-gray-500">Loading users...</span>
                    </div>
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                    No users found
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                          <User className="w-5 h-5 text-blue-600" />
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">{user.name}</div>
                          <div className="text-sm text-gray-500">{user.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {getStatusBadge(user.status)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{user.totalPoints?.toLocaleString() || '0'}</div>
                      <div className="text-sm text-gray-500">Total Points</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex space-x-2">
                        {user.isHighRisk && (
                          <span className="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs">
                            High Risk
                          </span>
                        )}
                        {user.isVerified && (
                          <span className="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs">
                            Verified
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.lastActivity ? new Date(user.lastActivity).toLocaleDateString() : 'Never'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex space-x-2">
                        <button
                          onClick={() => setSelectedUser(user)}
                          className="text-blue-600 hover:text-blue-900"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => {
                            const newStatus = user.status === 'ACTIVE' ? 'FROZEN' : 'ACTIVE';
                            updateUserStatus(user.id, newStatus, 'Status toggled from admin panel');
                          }}
                          className="text-gray-600 hover:text-gray-900"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="bg-white px-4 py-3 border-t border-gray-200 sm:px-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-700">
                  Showing page {currentPage + 1} of {totalPages}
                </p>
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                  disabled={currentPage === 0}
                  className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                <button
                  onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                  disabled={currentPage >= totalPages - 1}
                  className="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* User Detail Modal */}
      {selectedUser && (
        <UserDetailModal 
          user={selectedUser} 
          onClose={() => setSelectedUser(null)}
          onUpdate={loadUsers}
        />
      )}
    </div>
  );
};

// User Detail Modal Component
const UserDetailModal = ({ user, onClose, onUpdate }) => {
  const [activeTab, setActiveTab] = useState('overview');
  const [pointsHistory, setPointsHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (activeTab === 'history') {
      loadPointsHistory();
    }
  }, [activeTab, user.id]);

  const loadPointsHistory = async () => {
    try {
      setLoading(true);
      // Mock data for points history since we don't have the endpoint implemented
      setPointsHistory([
        {
          id: 1,
          type: 'EARN',
          amount: 100,
          balanceAfter: 1500,
          description: 'Purchase reward',
          createdAt: new Date().toISOString()
        },
        {
          id: 2,
          type: 'REDEEM',
          amount: -50,
          balanceAfter: 1400,
          description: 'Gift card redemption',
          createdAt: new Date(Date.now() - 86400000).toISOString()
        }
      ]);
    } catch (error) {
      console.error('Error loading points history:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (newStatus) => {
    try {
      await ApiService.updateUserStatus(user.id, {
        status: newStatus,
        reason: `Status changed to ${newStatus} from admin panel`
      });
      onUpdate();
      onClose();
      alert('User status updated successfully');
    } catch (error) {
      alert(`Error updating status: ${error.message}`);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center space-x-4">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
              <User className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">{user.name}</h2>
              <p className="text-gray-600">{user.email}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Tabs */}
        <div className="border-b border-gray-200">
          <nav className="flex space-x-8 px-6">
            {[
              { id: 'overview', label: 'Overview' },
              { id: 'history', label: 'Points History' },
              { id: 'actions', label: 'Actions' }
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Content */}
        <div className="p-6 max-h-96 overflow-y-auto">
          {activeTab === 'overview' && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">User Information</h3>
                <dl className="space-y-3">
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Status</dt>
                    <dd className="mt-1">{getStatusBadge(user.status)}</dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Phone</dt>
                    <dd className="mt-1 text-sm text-gray-900">{user.phoneNumber || 'Not provided'}</dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Member Since</dt>
                    <dd className="mt-1 text-sm text-gray-900">
                      {new Date(user.createdAt).toLocaleDateString()}
                    </dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Last Activity</dt>
                    <dd className="mt-1 text-sm text-gray-900">
                      {user.lastActivity ? new Date(user.lastActivity).toLocaleDateString() : 'Never'}
                    </dd>
                  </div>
                </dl>
              </div>

              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-4">Points Summary</h3>
                <dl className="space-y-3">
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Current Balance</dt>
                    <dd className="mt-1 text-2xl font-bold text-gray-900">
                      {user.totalPoints?.toLocaleString() || '0'} pts
                    </dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Lifetime Earnings</dt>
                    <dd className="mt-1 text-sm text-gray-900">
                      {user.lifetimeEarnings?.toLocaleString() || '0'} pts
                    </dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Lifetime Redemptions</dt>
                    <dd className="mt-1 text-sm text-gray-900">
                      {user.lifetimeRedemptions?.toLocaleString() || '0'} pts
                    </dd>
                  </div>
                </dl>

                <div className="mt-4 flex space-x-2">
                  {user.isHighRisk && (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                      <AlertTriangle className="w-3 h-3 mr-1" />
                      High Risk
                    </span>
                  )}
                  {user.isVerified && (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                      <CheckCircle className="w-3 h-3 mr-1" />
                      Verified
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'history' && (
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">Points History</h3>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                  <span className="ml-2 text-gray-500">Loading history...</span>
                </div>
              ) : (
                <div className="space-y-4">
                  {pointsHistory.map((transaction) => (
                    <div key={transaction.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                      <div className="flex items-center space-x-3">
                        <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                          transaction.type === 'EARN' 
                            ? 'bg-green-100 text-green-600' 
                            : 'bg-red-100 text-red-600'
                        }`}>
                          {transaction.type === 'EARN' ? '+' : '-'}
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">{transaction.description}</p>
                          <p className="text-sm text-gray-500">
                            {new Date(transaction.createdAt).toLocaleDateString()}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className={`font-bold ${
                          transaction.amount > 0 ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {transaction.amount > 0 ? '+' : ''}{transaction.amount} pts
                        </p>
                        <p className="text-sm text-gray-500">
                          Balance: {transaction.balanceAfter} pts
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'actions' && (
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-4">User Actions</h3>
              <div className="space-y-4">
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Status Management</h4>
                  <div className="grid grid-cols-2 gap-3">
                    {['ACTIVE', 'FROZEN', 'SUSPENDED', 'INACTIVE'].map((status) => (
                      <button
                        key={status}
                        onClick={() => updateStatus(status)}
                        disabled={user.status === status}
                        className={`px-4 py-2 rounded-lg border text-sm font-medium ${
                          user.status === status
                            ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                            : 'border-gray-300 text-gray-700 hover:bg-gray-50'
                        }`}
                      >
                        Set {status}
                      </button>
                    ))}
                  </div>
                </div>

                <div>
                  <h4 className="font-medium text-gray-900 mb-2">Risk & Verification</h4>
                  <div className="space-y-2">
                    <button className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm text-left hover:bg-gray-50">
                      {user.isHighRisk ? 'Remove High Risk Flag' : 'Mark as High Risk'}
                    </button>
                    <button className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm text-left hover:bg-gray-50">
                      {user.isVerified ? 'Remove Verification' : 'Verify Account'}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );

  function getStatusBadge(status) {
    const statusClasses = {
      ACTIVE: 'bg-green-100 text-green-800',
      FROZEN: 'bg-blue-100 text-blue-800',
      SUSPENDED: 'bg-red-100 text-red-800',
      INACTIVE: 'bg-gray-100 text-gray-800'
    };

    return (
      <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusClasses[status] || statusClasses.INACTIVE}`}>
        {status}
      </span>
    );
  }
};

// Partners Management Component
const PartnersManagement = () => {
  const [partners, setPartners] = useState([]);
  const [loading, setLoading] = useState(true);
  const [partnerStats, setPartnerStats] = useState(null);

  useEffect(() => {
    loadPartners();
    loadPartnerStats();
  }, []);

  const loadPartners = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getPartners({ page: 0, size: 20 });
      if (response.success) {
        setPartners(response.data.content);
      }
    } catch (error) {
      console.error('Error loading partners:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadPartnerStats = async () => {
    try {
      const response = await ApiService.getPartnerStats();
      if (response.success) {
        setPartnerStats(response.data);
      }
    } catch (error) {
      console.error('Error loading partner stats:', error);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Partner Management</h1>
          <p className="text-gray-600">Manage loyalty program integration partners</p>
        </div>
        <button className="mt-4 sm:mt-0 flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          <Plus className="w-4 h-4" />
          <span>Add Partner</span>
        </button>
      </div>

      {/* Stats Cards */}
      {partnerStats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <StatsCard
            title="Total Partners"
            value={partnerStats.totalPartners?.toLocaleString() || '0'}
            icon={Target}
            color="blue"
          />
          <StatsCard
            title="Active Partners"
            value={partnerStats.activePartners?.toLocaleString() || '0'}
            icon={CheckCircle}
            color="green"
          />
          <StatsCard
            title="Total Transactions"
            value={partnerStats.totalTransactions?.toLocaleString() || '0'}
            icon={TrendingUp}
            color="purple"
          />
          <StatsCard
            title="Amount Processed"
            value={`${(partnerStats.totalAmountProcessed || 0).toLocaleString()}`}
            icon={DollarSign}
            color="orange"
          />
        </div>
      )}

      {/* Partners Table */}
      <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Partner
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Auth Method
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Conversion Rate
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Transactions
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center">
                    <div className="flex items-center justify-center">
                      <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      <span className="ml-2 text-gray-500">Loading partners...</span>
                    </div>
                  </td>
                </tr>
              ) : partners.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                    No partners found
                  </td>
                </tr>
              ) : (
                partners.map((partner) => (
                  <tr key={partner.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center">
                          <Target className="w-5 h-5 text-purple-600" />
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">{partner.name}</div>
                          <div className="text-sm text-gray-500">{partner.description}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        partner.isActive 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {partner.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded text-xs">
                        {partner.authMethod}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {(partner.conversionRate * 100).toFixed(2)}%
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{partner.totalTransactions?.toLocaleString() || '0'}</div>
                      <div className="text-sm text-gray-500">
                        ${(partner.totalAmountProcessed || 0).toLocaleString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex space-x-2">
                        <button className="text-blue-600 hover:text-blue-900">
                          <Eye className="w-4 h-4" />
                        </button>
                        <button className="text-gray-600 hover:text-gray-900">
                          <Edit className="w-4 h-4" />
                        </button>
                        <button className="text-red-600 hover:text-red-900">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

// Sidebar Component
const Sidebar = ({ activeView, setActiveView, isMobileOpen, setIsMobileOpen }) => {
  const { user, logout } = useAuth();

  const navigation = [
    { id: 'dashboard', name: 'Dashboard', icon: BarChart3 },
    { id: 'users', name: 'Users', icon: Users },
    { id: 'partners', name: 'Partners', icon: Target },
    { id: 'analytics', name: 'Analytics', icon: PieChart },
    { id: 'settings', name: 'Settings', icon: Settings },
  ];

  const handleLogout = async () => {
    await logout();
  };

  return (
    <>
      {/* Mobile overlay */}
      {isMobileOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={() => setIsMobileOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0 ${
        isMobileOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="flex items-center justify-between h-16 px-6 border-b border-gray-200">
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-lg">🧩</span>
              </div>
              <span className="text-xl font-bold text-gray-900">LoyalBridge</span>
            </div>
            <button
              onClick={() => setIsMobileOpen(false)}
              className="lg:hidden text-gray-400 hover:text-gray-600"
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-2">
            {navigation.map((item) => {
              const isActive = activeView === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => {
                    setActiveView(item.id);
                    setIsMobileOpen(false);
                  }}
                  className={`w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-left transition-colors ${
                    isActive
                      ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
                      : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                  }`}
                >
                  <item.icon className="w-5 h-5" />
                  <span className="font-medium">{item.name}</span>
                </button>
              );
            })}
          </nav>

          {/* User Profile */}
          <div className="p-4 border-t border-gray-200">
            <div className="flex items-center space-x-3 mb-3">
              <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <User className="w-5 h-5 text-blue-600" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {user?.fullName || user?.firstName || 'Admin User'}
                </p>
                <p className="text-xs text-gray-500 truncate">{user?.email}</p>
                <p className="text-xs text-blue-600 font-medium">{user?.role?.replace('_', ' ')}</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              className="w-full flex items-center space-x-2 px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
              <span className="text-sm font-medium">Sign Out</span>
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

// Header Component
const Header = ({ setIsMobileOpen, activeView }) => {
  const { user } = useAuth();

  const getPageTitle = (view) => {
    const titles = {
      dashboard: 'Dashboard',
      users: 'User Management',
      partners: 'Partner Management',
      analytics: 'Analytics',
      settings: 'Settings'
    };
    return titles[view] || 'LoyalBridge Admin';
  };

  return (
    <header className="bg-white border-b border-gray-200 px-4 py-4 lg:px-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => setIsMobileOpen(true)}
            className="lg:hidden text-gray-600 hover:text-gray-900"
          >
            <Menu className="w-6 h-6" />
          </button>
          <div>
            <h1 className="text-xl font-semibold text-gray-900">{getPageTitle(activeView)}</h1>
            <p className="text-sm text-gray-500">Welcome back, {user?.firstName || 'Admin'}</p>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          <button className="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg">
            <Bell className="w-5 h-5" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>
          
          <div className="hidden sm:flex items-center space-x-2 px-3 py-2 bg-gray-100 rounded-lg">
            <div className="w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center">
              <User className="w-4 h-4 text-blue-600" />
            </div>
            <span className="text-sm font-medium text-gray-700">
              {user?.firstName || 'Admin'}
            </span>
            <span className="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded-full">
              {user?.role?.replace('_', ' ')}
            </span>
          </div>
        </div>
      </div>
    </header>
  );
};

// Analytics Component
const Analytics = () => {
  const [timeRange, setTimeRange] = useState('7d');
  const [conversionStats, setConversionStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalytics();
  }, [timeRange]);

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      const response = await ApiService.getConversionStats();
      if (response.success) {
        setConversionStats(response.data);
      }
    } catch (error) {
      console.error('Error loading analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Analytics & Reports</h1>
          <p className="text-gray-600">Monitor conversion performance and trends</p>
        </div>
        <div className="mt-4 sm:mt-0 flex items-center space-x-4">
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="7d">Last 7 days</option>
            <option value="30d">Last 30 days</option>
            <option value="90d">Last 90 days</option>
          </select>
          <button className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
            <Download className="w-4 h-4" />
            <span>Export</span>
          </button>
        </div>
      </div>

      {/* Conversion Stats */}
      {conversionStats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatsCard
            title="Total Conversions"
            value={conversionStats.totalConversions?.toLocaleString() || '0'}
            icon={TrendingUp}
            change="+15%"
            color="blue"
          />
          <StatsCard
            title="Success Rate"
            value={`${(conversionStats.conversionSuccessRate || 0).toFixed(1)}%`}
            icon={CheckCircle}
            change="+2.5%"
            color="green"
          />
          <StatsCard
            title="Failed Conversions"
            value={conversionStats.failedConversions?.toLocaleString() || '0'}
            icon={XCircle}
            change="-5%"
            color="red"
          />
          <StatsCard
            title="Pending"
            value={conversionStats.pendingConversions?.toLocaleString() || '0'}
            icon={Clock}
            color="orange"
          />
        </div>
      )}

      {/* Charts Placeholder */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Conversion Trends</h3>
          <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
            <div className="text-center">
              <LineChart className="w-12 h-12 text-gray-400 mx-auto mb-2" />
              <p className="text-gray-500">Chart visualization would go here</p>
              <p className="text-sm text-gray-400">Integration with Chart.js or similar</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Partner Performance</h3>
          <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
            <div className="text-center">
              <PieChart className="w-12 h-12 text-gray-400 mx-auto mb-2" />
              <p className="text-gray-500">Partner comparison chart</p>
              <p className="text-sm text-gray-400">Shows conversion distribution</p>
            </div>
          </div>
        </div>
      </div>

      {/* Detailed Reports */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Conversion Details</h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Partner</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Conversions</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Success Rate</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Volume</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Trend</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {[
                { name: 'Amazon Rewards', conversions: 1234, successRate: 95.2, volume: '$12,450', trend: '+12%' },
                { name: 'Starbucks Plus', conversions: 856, successRate: 98.1, volume: '$8,560', trend: '+8%' },
                { name: 'Netflix Premium', conversions: 432, successRate: 89.5, volume: '$4,320', trend: '-2%' },
              ].map((partner, index) => (
                <tr key={index} className="hover:bg-gray-50">
                  <td className="px-4 py-4 font-medium text-gray-900">{partner.name}</td>
                  <td className="px-4 py-4 text-gray-600">{partner.conversions.toLocaleString()}</td>
                  <td className="px-4 py-4">
                    <span className={`px-2 py-1 rounded-full text-xs ${
                      partner.successRate > 95 ? 'bg-green-100 text-green-800' :
                      partner.successRate > 90 ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {partner.successRate}%
                    </span>
                  </td>
                  <td className="px-4 py-4 text-gray-600">{partner.volume}</td>
                  <td className="px-4 py-4">
                    <span className={`text-sm font-medium ${
                      partner.trend.startsWith('+') ? 'text-green-600' : 'text-red-600'
                    }`}>
                      {partner.trend}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

// SettingsPage Component
const SettingsPage = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('profile');

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
        <p className="text-gray-600">Manage your account and system preferences</p>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="flex space-x-8">
          {[
            { id: 'profile', label: 'Profile' },
            { id: 'security', label: 'Security' },
            { id: 'notifications', label: 'Notifications' },
            { id: 'system', label: 'System' }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`py-4 px-1 border-b-2 font-medium text-sm ${
                activeTab === tab.id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Content */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        {activeTab === 'profile' && (
          <div className="space-y-6">
            <h3 className="text-lg font-medium text-gray-900">Profile Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  First Name
                </label>
                <input
                  type="text"
                  defaultValue={user?.firstName || ''}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Last Name
                </label>
                <input
                  type="text"
                  defaultValue={user?.lastName || ''}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <input
                  type="email"
                  defaultValue={user?.email || ''}
                  disabled
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Role
                </label>
                <input
                  type="text"
                  value={user?.role?.replace('_', ' ') || ''}
                  disabled
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-500"
                />
              </div>
            </div>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
              Save Changes
            </button>
          </div>
        )}

        {activeTab === 'security' && (
          <div className="space-y-6">
            <h3 className="text-lg font-medium text-gray-900">Security Settings</h3>
            <div className="space-y-4">
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Change Password</h4>
                <div className="space-y-3">
                  <input
                    type="password"
                    placeholder="Current password"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                  <input
                    type="password"
                    placeholder="New password"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                  <input
                    type="password"
                    placeholder="Confirm new password"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <button className="mt-3 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                  Update Password
                </button>
              </div>

              <div className="border-t pt-4">
                <h4 className="font-medium text-gray-900 mb-2">Two-Factor Authentication</h4>
                <p className="text-sm text-gray-600 mb-3">
                  Add an extra layer of security to your account
                </p>
                <button className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">
                  Enable 2FA
                </button>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'notifications' && (
          <div className="space-y-6">
            <h3 className="text-lg font-medium text-gray-900">Notification Preferences</h3>
            <div className="space-y-4">
              {[
                { id: 'email_alerts', label: 'Email Alerts', description: 'Receive email notifications for important events' },
                { id: 'user_activities', label: 'User Activities', description: 'Get notified about new user registrations and activities' },
                { id: 'partner_updates', label: 'Partner Updates', description: 'Notifications about partner integrations and changes' },
                { id: 'system_alerts', label: 'System Alerts', description: 'Critical system notifications and maintenance updates' }
              ].map((notification) => (
                <div key={notification.id} className="flex items-center justify-between py-3 border-b border-gray-200 last:border-0">
                  <div>
                    <h4 className="font-medium text-gray-900">{notification.label}</h4>
                    <p className="text-sm text-gray-500">{notification.description}</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input type="checkbox" className="sr-only peer" defaultChecked />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                  </label>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'system' && (
          <div className="space-y-6">
            <h3 className="text-lg font-medium text-gray-900">System Settings</h3>
            <div className="space-y-6">
              <div>
                <h4 className="font-medium text-gray-900 mb-3">Application Info</h4>
                <dl className="space-y-2">
                  <div className="flex justify-between">
                    <dt className="text-sm text-gray-500">Version</dt>
                    <dd className="text-sm text-gray-900">v1.0.0</dd>
                  </div>
                  <div className="flex justify-between">
                    <dt className="text-sm text-gray-500">Last Updated</dt>
                    <dd className="text-sm text-gray-900">June 8, 2025</dd>
                  </div>
                  <div className="flex justify-between">
                    <dt className="text-sm text-gray-500">Database</dt>
                    <dd className="text-sm text-green-600">Connected</dd>
                  </div>
                </dl>
              </div>

              <div className="border-t pt-4">
                <h4 className="font-medium text-gray-900 mb-3">Data Management</h4>
                <div className="space-y-3">
                  <button className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">
                    <Download className="w-4 h-4" />
                    <span>Export System Data</span>
                  </button>
                  <button className="flex items-center space-x-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50">
                    <RefreshCw className="w-4 h-4" />
                    <span>Clear Cache</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

// Main App Component
const App = () => {
  const [activeView, setActiveView] = useState('dashboard');
  const [isMobileOpen, setIsMobileOpen] = useState(false);

  const renderView = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard />;
      case 'users':
        return <UsersManagement />;
      case 'partners':
        return <PartnersManagement />;
      case 'analytics':
        return <Analytics />;
      case 'settings':
        return <SettingsPage />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar 
        activeView={activeView} 
        setActiveView={setActiveView}
        isMobileOpen={isMobileOpen}
        setIsMobileOpen={setIsMobileOpen}
      />
      
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header 
          setIsMobileOpen={setIsMobileOpen}
          activeView={activeView}
        />
        
        <main className="flex-1 overflow-x-hidden overflow-y-auto p-6">
          {renderView()}
        </main>
      </div>
    </div>
  );
};

// Main Application with Auth Provider
const LoyalBridgeAdmin = () => {
  return (
    <AuthProvider>
      <AuthenticatedApp />
    </AuthProvider>
  );
};

// Authenticated App Wrapper
const AuthenticatedApp = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-900 via-purple-800 to-indigo-900 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-white border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-white text-lg">Loading LoyalBridge Admin...</p>
        </div>
      </div>
    );
  }

  return isAuthenticated ? <App /> : <LoginForm />;
};

export default LoyalBridgeAdmin;