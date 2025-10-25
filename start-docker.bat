@echo off
echo ========================================
echo Iniciando 3 Receptores com Docker
echo ========================================
echo.

echo [1/2] Parando containers antigos...
docker-compose down 2>nul

echo.
echo [2/2] Iniciando 3 receptores...
docker-compose up --build -d

echo.
echo Aguardando receptores iniciarem...
timeout /t 5 /nobreak > nul

echo.
echo ========================================
echo Receptores rodando em:
echo   - Receptor 1: localhost:12345
echo   - Receptor 2: localhost:12346
echo   - Receptor 3: localhost:12347
echo ========================================
echo.
echo Ver logs:
echo   docker-compose logs -f
echo.
echo Parar:
echo   docker-compose down
echo.
pause
