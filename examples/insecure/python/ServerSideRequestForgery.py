def is_their_service_broken(url: str) -> bool:
    try:
        host = requests.utils.urlparse(url).hostname
        res = socket.gethostbyname(host)
    except socket.gaierror:
        return False
    return not ipaddress.ip_address(res).is_private

@app.route('/avatar/<string:avatar>')
def fetch_avatar(avatar: str) -> Response:
    avatar = f'http://unstable-avatar-service.tld{avatar}'
    # This service is still in development and their DNS sometimes
    # point to their own internal network, make sure it's OK
    if not is_their_service_broken(avatar): 
        hash = md5(avatar.encode("ascii")).hexdigest()
        avatar = f'http://www.gravatar.com/avatar/{hash}'
    res = requests.get(avatar, stream=True, timeout=1)
    return make_response(
        stream_with_context(res),
        res.status_code,
        {'Content-Type': 'image/png'}
    )