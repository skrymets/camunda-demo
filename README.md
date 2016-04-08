# camunda-demo
Caminda BPM investigation sandbox

activemq start -Dorg.apache.activemq.SERIALIZABLE_PACKAGES=*



pscp camunda-demo-***.jar web@<REMOTE_HOST>:/home/web/camunda-demo.jar

ssh web@<REMOTE_HOST>

cd ~
chmod +x camunda-demo.ja
sudo ln -s /home/web/camunda-demo.jar /etc/init.d/camunda-demo

sudo mkdir /var/run/camunda-demo
chown web:web /var/run/camunda-demo
echo pidFolder=/var/run/camunda-demo  > /home/web/camunda-demo.jar.conf

sudo su
echo 0 > /var/log/camunda-demo.log
chown web:web /var/log/camunda-demo.log
^D

/etc/init.d/camunda-demo start
tail -f /var/log/camunda-demo.log
