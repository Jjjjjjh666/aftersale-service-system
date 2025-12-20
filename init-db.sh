#!/bin/bash
echo "初始化数据库..."
read -p "请输入MySQL root密码: " -s mysql_pwd
echo ""

mysql -uroot -p"$mysql_pwd" < sql/aftersale_db.sql
if [ $? -eq 0 ]; then
    echo "✅ aftersale_db 初始化成功"
else
    echo "❌ aftersale_db 初始化失败"
    exit 1
fi

mysql -uroot -p"$mysql_pwd" < sql/service_db.sql
if [ $? -eq 0 ]; then
    echo "✅ service_db 初始化成功"
else
    echo "❌ service_db 初始化失败"
    exit 1
fi

echo ""
echo "数据库初始化完成！"
