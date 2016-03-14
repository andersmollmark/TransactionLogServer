#!/bin/bash

function addService ()
{
	if [ "`rc-update show | grep $1 | grep vc`" == "" ] ; then
		rc-update add $1 vc
	fi ;
}
set -e

echo "Installing utls"

echo "changing rights in /tmp"
chown vms:vms /tmp
chmod a+w /tmp
rm -r /opt/utls/dist

if [ ! -e /opt/utls ] ; then
	mkdir /opt/utls
	chown vms:vms /opt/utls
fi

if [ ! -e /opt/utls/dist ] ; then
	mkdir /opt/utls/dist
	chown vms:vms /opt/utls/dist
fi

if [ ! -e /etc/runlevels/vc ] ; then
	mkdir /etc/runlevels/vc
fi

if [ ! -e /var/log/utls ] ; then
	mkdir /var/log/utls
	chown vms:vms /var/log/utls
fi

echo "untaring utlsdist"
tar -xzf /tmp/utlsdist.tar.gz -C /opt
echo "untaring activemq"
tar -xzf /tmp/activemq-bin.tar.gz -C /opt/utls

if [ ! -h /etc/init.d/publishUTLServer ] ; then
	ln -s /opt/utls/script/init.d/publishUTLServer /etc/init.d/publishUTLServer
fi

if [ ! -h /etc/init.d/utlServer ] ; then
	ln -s /opt/utls/script/init.d/utlServer /etc/init.d/utlServer
fi

if [ ! -h /etc/init.d/activemq ] ; then
	ln -s /opt/utls/script/init.d/activemq /etc/init.d/activemq
fi

if [ ! -h /opt/utls/bin/lib ] ; then
	ln -s /opt/utls/lib /opt/utls/bin/lib
fi

if [ ! -h /var/lib/mysql ] ; then
	echo "linking mysql"
	ln -s /var/log/mysqlDb /var/lib/mysql
fi

cp /opt/utls/mysql/mysqlaccess.conf /etc/mysql/mysqlaccess.conf
chown root:root /etc/mysql/mysqlaccess.conf
chmod 644 /etc/mysql/mysqlaccess.conf
chmod +x /opt/utls/dist/UserTransactionLogServer.jar
#chmod g+x,u+x /opt/utls/script/init.d/publishUTLServer
#chmod g+x,u+x /opt/utls/script/init.d/utlServer
#chmod g+x,u+x /opt/utls/script/init.d/activemq

echo "Stop services"
/etc/init.d/mysql stop
/etc/init.d/activemq stop >/dev/null 2>&1
/etc/init.d/publishUTLServer stop >/dev/null 2>&1
/etc/init.d/utlServer stop >/dev/null 2>&1

echo "setting jdk-version"
eselect java-vm set system oracle-jdk-bin-1.8

echo "Add services to runlevel vc"
addService vixie-cron
addService net.net0
addService syslog-ng
addService local
addService dnsmasq
addService xinetd
addService sshd
addService pure-ftpd
addService atd
addService dbus
addService avahi-daemon
addService ntpd
addService utlServer
addService publishUTLServer
addService activemq
addService mysql
rc-update -u

#echo starting new daemons >>/var/log/utls/utlsinstall.log

echo "Starting services"
/etc/init.d/mysql start

mysql --user=root --password=delavalpwd -e "CREATE DATABASE IF NOT EXISTS user_transaction_log_server"
mysql --user=root --password=delavalpwd -e "GRANT SUPER ON *.* TO logAdmin@localhost IDENTIFIED BY 'admin'"



#/etc/init.d/activemq start
#/etc/init.d/utlServer start
#/etc/init.d/publishUTLServer start

rc vc

#echo Install finished `date` >>/var/log/utls/utlsinstall.log