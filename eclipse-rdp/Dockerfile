#FROM lscr.io/linuxserver/rdesktop:ubuntu-xfce
FROM lscr.io/linuxserver/webtop:ubuntu-xfce

ENV PUID=1000 
ENV PGID=1000
ENV TZ=Etc/UTC 

EXPOSE 3000
EXPOSE 3001
#EXPOSE 3389

RUN apt update
RUN apt install git -y
RUN apt install wget -y
RUN apt install openjdk-21-jdk -y

RUN wget 'https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2024-03/R/eclipse-modeling-2024-03-R-linux-gtk-x86_64.tar.gz&r=1' -O eclipse.tar.gz
RUN tar -xzf eclipse.tar.gz -C /opt
RUN rm eclipse.tar.gz

RUN /opt/eclipse/eclipse -application org.eclipse.equinox.p2.director -repository https://download.eclipse.org/releases/2024-03 -installIU org.eclipse.collections.feature.source.feature.group,org.eclipse.xtend.sdk.feature.group
RUN /opt/eclipse/eclipse -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/viatra/updates/release/latest -installIU org.eclipse.viatra.query.patternlanguage.emf,org.eclipse.viatra.query.runtime,org.eclipse.viatra.query.runtime.rete,org.eclipse.viatra.query.runtime.localsearch,org.eclipse.viatra.addon.querybasedfeatures.runtime,org.eclipse.viatra.addon.validation.core,org.eclipse.viatra.addon.validation.runtime

RUN echo '[Desktop Entry]\n\
Version=13.0\n\
Type=Application\n\
Terminal=false\n\
Icon=/opt/eclipse/icon.xpm\n\
Name=Modeling 2024-03\n\
Exec=/opt/eclipse/eclipse\n\
Name=Modeling 2024-03\n\
Categories=Development\n\' > /usr/share/applications/eclipse.desktop

RUN apt install ant -y

COPY  eclipse-workspace /config/eclipse-workspace

RUN chmod -R o+rwx /config/eclipse-workspace

RUN wget https://download-cdn.jetbrains.com/idea/ideaIC-2023.3.6.tar.gz
RUN tar -xzf ideaIC-*.tar.gz -C /opt
RUN rm ideaIC-2023.3.6.tar.gz

RUN echo '[Desktop Entry]\n\
Version=13.0\n\
Type=Application\n\
Terminal=false\n\
Icon[en_US]=/opt/idea-IC-233.15026.9/bin/idea.png\n\
Name[en_US]=IntelliJ\n\
Exec=/opt/idea-IC-233.15026.9/bin/idea.sh\n\
Name=IntelliJ\n\
Categories=Development\n\
Icon=/opt/idea-IC-233.15026.9/bin/idea.png' > /usr/share/applications/intellij.desktop

RUN rm -rf /config/.cache

#RUN chown -R abc /opt

#RUN chmod 644 /usr/share/applications/intellij.desktop

COPY refinery /config/refinery

RUN chmod -R o+rwx /config/refinery

