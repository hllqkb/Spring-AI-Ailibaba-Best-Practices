import { API } from '@/services/typings';
import { request } from '@umijs/max';

export async function getResource(params: { id: string }): Promise<API.Response<API.FileResourceVO>> {
  return request('/api/originFileResource/get', {
    method: 'GET',
    params: { resourceId: params.id },
  });
}

export async function uploadChatFile(file: File): Promise<API.Response<API.FileResourceVO>> {
  const formData = new FormData();
  formData.append('file', file);
  return request('/api/originFileResource/upload', {
    method: 'POST',
    data: formData,
  });
}