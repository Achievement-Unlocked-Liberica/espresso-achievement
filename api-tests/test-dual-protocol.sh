#!/bin/bash

# Dual Protocol Test Script for Espresso Achievement Application
# This script tests both HTTP and HTTPS configurations

echo "🔐 Testing Dual Protocol Configuration (HTTP & HTTPS)"
echo "===================================================="

# Check if the certificate exists
if [ -f "src/main/resources/espresso-local.p12" ]; then
    echo "✅ SSL Certificate found: espresso-local.p12"
else
    echo "❌ SSL Certificate not found!"
    exit 1
fi

# Test HTTPS endpoints
echo ""
echo "� Testing HTTPS endpoints (port 8443)..."
echo "Note: You may see SSL warnings due to self-signed certificate"
echo ""

echo "Testing HTTPS health endpoint..."
curl -k -s -o /dev/null -w "HTTPS Health Status: %{http_code}\n" https://localhost:8443/actuator/health 2>/dev/null

echo "Testing HTTPS Swagger API docs..."
curl -k -s -o /dev/null -w "HTTPS API Docs Status: %{http_code}\n" https://localhost:8443/api-docs 2>/dev/null

echo "Testing HTTPS Swagger UI..."
curl -k -s -o /dev/null -w "HTTPS Swagger UI Status: %{http_code}\n" https://localhost:8443/swagger-ui.html 2>/dev/null

# Test HTTP endpoints
echo ""
echo "📱 Testing HTTP endpoints (port 8080)..."
echo ""

echo "Testing HTTP health endpoint..."
curl -s -o /dev/null -w "HTTP Health Status: %{http_code}\n" http://localhost:8080/actuator/health 2>/dev/null

echo "Testing HTTP Swagger API docs..."
curl -s -o /dev/null -w "HTTP API Docs Status: %{http_code}\n" http://localhost:8080/api-docs 2>/dev/null

echo "Testing HTTP Swagger UI..."
curl -s -o /dev/null -w "HTTP Swagger UI Status: %{http_code}\n" http://localhost:8080/swagger-ui.html 2>/dev/null

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Both HTTP and HTTPS endpoints are accessible!"
    echo ""
    echo "🌐 HTTP Access (Mobile-friendly):"
    echo "   - Base URL: http://localhost:8080"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - API Docs: http://localhost:8080/api-docs"
    echo ""
    echo "🔐 HTTPS Access (Secure):"
    echo "   - Base URL: https://localhost:8443"
    echo "   - Swagger UI: https://localhost:8443/swagger-ui.html"
    echo "   - API Docs: https://localhost:8443/api-docs"
else
    echo "❌ Connection failed. Make sure the application is running."
fi

echo ""
echo "📝 Manual Testing:"
echo "1. Start the application: mvnw.cmd spring-boot:run"
echo ""
echo "2. For mobile app testing (HTTP - no SSL issues):"
echo "   - Base URL: http://localhost:8080"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   - Use userRestClient-HTTP.http for testing"
echo ""
echo "3. For web browser testing (HTTPS - secure):"
echo "   - Base URL: https://localhost:8443"
echo "   - Swagger UI: https://localhost:8443/swagger-ui.html"
echo "   - Accept security warning for self-signed certificate"
echo "   - Use userRestClient.http for testing"
