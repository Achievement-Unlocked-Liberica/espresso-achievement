#!/bin/bash

# HTTPS Test Script for Espresso Achievement Application
# This script tests the HTTPS configuration

echo "🔐 Testing HTTPS Configuration for Espresso Achievement"
echo "=================================================="

# Check if the certificate exists
if [ -f "src/main/resources/espresso-local.p12" ]; then
    echo "✅ SSL Certificate found: espresso-local.p12"
else
    echo "❌ SSL Certificate not found!"
    exit 1
fi

# Check if the application is running
echo "🚀 Testing HTTPS connection..."
echo "Note: You may see SSL warnings due to self-signed certificate"
echo ""

# Test with curl (ignore certificate errors for self-signed cert)
curl -k -s -o /dev/null -w "HTTPS Status: %{http_code}\n" https://localhost:8443/actuator/health 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ HTTPS connection successful!"
else
    echo "❌ HTTPS connection failed. Make sure the application is running."
fi

echo ""
echo "📝 Manual Testing:"
echo "1. Start the application: mvnw.cmd spring-boot:run"
echo "2. Open browser: https://localhost:8443"
echo "3. Accept security warning for self-signed certificate"
echo "4. Test API: https://localhost:8443/swagger-ui.html"
