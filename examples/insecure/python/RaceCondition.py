def upload(request):
    f = request.FILES["data"]
    with open(f'/tmp/storage/{f.name}', 'wb+') as destination:
        for chunk in f.chunks(): destination.write(chunk)
    return HttpResponse("File is uploaded!")

def install(request):
    language_name = request.GET['language_name']
    if '..' in language_name: return HttpResponse("Not allowed!")

    src = os.path.join('contrib', 'languages', language_name)
    dst = os.path.join('/tmp/extract', language_name)

    shutil.copy(src, dst)
    shutil.unpack_archive(dst, extract_dir='/tmp/extract')

    return HttpResponse("Installed!")

def clean(request):
    file = os.path.basename(request.GET['file'])
    file_safe = f'/tmp/storage/{file}'
    os.unlink(file_safe)
    return HttpResponse("file removed!")