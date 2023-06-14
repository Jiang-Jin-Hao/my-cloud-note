function sleep(ms) {
    let time = new Date().getTime();
    while (new Date().getTime() < time + ms) {
    }
}

function mySimpleDateFormat(date) {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    const second = date.getSeconds().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

function delAllCookie() {
    //清空全部cookie
    let keys = document.cookie.match(/[^ =;]+(?=\=)/g);
    if (keys) {
        for (let i = keys.length; i--;) {
            document.cookie =
                keys[i] + "=0;path=/;expires=" + new Date(0).toUTCString(); //清除当前域名下
            document.cookie =
                keys[i] + "=0;path=/;domain=" + document.domain + ";expires=" + new Date(0).toUTCString();
            document.cookie =
                keys[i] + "=0;path=/;expires=" + new Date(0).toUTCString();
        }
    }
}