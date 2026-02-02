import { Provider, useDispatch } from 'react-redux';
import configureStore from 'misc/redux/configureStore';
import React, { useEffect, useState } from 'react';

import App from './containers/App';
import rootReducer from './reducers';
import { RECEIVE_USER } from './constants/actionTypes'; 

const store = configureStore(rootReducer);
const GATEWAY_URL = process.env.REACT_APP_GATEWAY_URL || "http://34.79.215.93.nip.io";

const SecurityCheck = ({ children }) => {
  const [authorized, setAuthorized] = useState(false);
  const [loading, setLoading] = useState(true);
  const dispatch = useDispatch();

  useEffect(() => {
    fetch(`${GATEWAY_URL}/profile`, {
      method: "GET",
      credentials: "include",
    })
      .then((res) => {
        if (res.status === 200) {
          return res.json();
        } else {
          throw new Error("Unauthorized");
        }
      })
      .then((data) => {
        console.log("Logged in as:", data.name);
        dispatch({
          type: RECEIVE_USER,
          payload: {
            firstName: data.name,
            lastName: '',
            login: data.email,
            email: data.email,
            authorities: ['USER'],
            id: data.sub || 'google-user'
          }
        });

        setAuthorized(true);
      })
      .catch((e) => {
        setAuthorized(false);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [dispatch]);

  const handleLogin = () => {
    window.location.href = `${GATEWAY_URL}/oauth2/authorization/google`;
  };

  if (loading) {
    return <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px' }}>Loading...</div>;
  }

  if (!authorized) {
    return (
      <div style={{ 
        height: '100vh', 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center', 
        alignItems: 'center',
        backgroundColor: '#f0f2f5',
        fontFamily: 'Arial, sans-serif'
      }}>
        <h1>Welcome to Movie App 🎬</h1>
        <p>Please sign in to continue</p>
        <button 
          onClick={handleLogin}
          style={{
            padding: "12px 24px",
            fontSize: "16px",
            backgroundColor: "#4285F4",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
            fontWeight: "bold",
            marginTop: "20px"
          }}
        >
          Sign in with Google
        </button>
      </div>
    );
  }

  return children;
};

export default function Index() {
  return (
    <Provider store={store}>
      <SecurityCheck>
        <App />
      </SecurityCheck>
    </Provider>
  );
}